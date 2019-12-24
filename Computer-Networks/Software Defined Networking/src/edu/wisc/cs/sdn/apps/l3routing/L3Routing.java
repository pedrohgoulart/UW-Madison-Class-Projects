package edu.wisc.cs.sdn.apps.l3routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openflow.protocol.instruction.OFInstruction;
import org.openflow.protocol.instruction.OFInstructionApplyActions;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.OFMatch;

import edu.wisc.cs.sdn.apps.util.Host;
import edu.wisc.cs.sdn.apps.util.SwitchCommands;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitch.PortChangeType;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.ImmutablePort;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceListener;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryListener;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.routing.Link;

public class L3Routing implements IFloodlightModule, IOFSwitchListener, 
		ILinkDiscoveryListener, IDeviceListener
{
	public static final String MODULE_NAME = L3Routing.class.getSimpleName();
	
	// Interface to the logging system
    private static Logger log = LoggerFactory.getLogger(MODULE_NAME);
    
    // Interface to Floodlight core for interacting with connected switches
    private IFloodlightProviderService floodlightProv;

    // Interface to link discovery service
    private ILinkDiscoveryService linkDiscProv;

    // Interface to device manager service
    private IDeviceService deviceProv;
    
    // Switch table in which rules should be installed
    public static byte table;
    
    // Map of hosts to devices
    private Map<IDevice,Host> knownHosts;

	/**
     * Loads dependencies and initializes data structures.
     */
	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		log.info(String.format("Initializing %s...", MODULE_NAME));
		Map<String,String> config = context.getConfigParams(this);
        table = Byte.parseByte(config.get("table"));
        
		this.floodlightProv = context.getServiceImpl(IFloodlightProviderService.class);
        this.linkDiscProv = context.getServiceImpl(ILinkDiscoveryService.class);
        this.deviceProv = context.getServiceImpl(IDeviceService.class);
        
        this.knownHosts = new ConcurrentHashMap<IDevice,Host>();
	}

	/**
     * Subscribes to events and performs other startup tasks.
     */
	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		log.info(String.format("Starting %s...", MODULE_NAME));
		this.floodlightProv.addOFSwitchListener(this);
		this.linkDiscProv.addListener(this);
		this.deviceProv.addListener(this);
	}
	
    /**
     * Get a list of all known hosts in the network.
     */
    private Collection<Host> getHosts() { return this.knownHosts.values(); }
	
    /**
     * Get a map of all active switches in the network. Switch DPID is used as
     * the key.
     */
	private Map<Long, IOFSwitch> getSwitches() { return floodlightProv.getAllSwitchMap(); }
	
    /**
     * Get a list of all active links in the network.
     */
    private Collection<Link> getLinks() { return linkDiscProv.getLinks().keySet(); }

    /**
     * Event handler called when a host joins the network.
     * @param device information about the host
     */
	@Override
	public void deviceAdded(IDevice device) {
		Host host = new Host(device, this.floodlightProv);
		// We only care about a new host if we know its IP
		if (host.getIPv4Address() != null) {
			log.info(String.format("Host %s added", host.getName()));
			this.knownHosts.put(device, host);
			
			// Add rules to route to new host
			this.setRouterAndHostRules(host);
		}
	}

	/**
     * Event handler called when a host is no longer attached to a switch.
     * @param device information about the host
     */
	@Override
	public void deviceRemoved(IDevice device) {
		Host host = this.knownHosts.get(device);
		if (null == host) return;
		
		this.knownHosts.remove(device);
		
		log.info(String.format("Host %s is no longer attached to a switch", host.getName()));
		
		// Remove rules to route to host
		this.removeSwitchRules(host);
	}

	/**
     * Event handler called when a host moves within the network.
     * @param device information about the host
     */
	@Override
	public void deviceMoved(IDevice device) {
		Host host = this.knownHosts.get(device);
		
		if (null == host) {
			host = new Host(device, this.floodlightProv);
			this.knownHosts.put(device, host);
		}
		
		if (!host.isAttachedToSwitch()) {
			this.deviceRemoved(device);
			return;
		}
		
		log.info(String.format("Host %s moved to s%d:%d", host.getName(),
				host.getSwitch().getId(), host.getPort()));
		
		// Change (reset) rules to route to host
		this.removeSwitchRules(host);
		this.setRouterAndHostRules(host);
	}
	
    /**
     * Event handler called when a switch joins the network.
     * @param DPID for the switch
     */
	@Override		
	public void switchAdded(long switchId) {
		IOFSwitch sw = this.floodlightProv.getSwitch(switchId);
		log.info(String.format("Switch s%d added", switchId));
		
		// Change routing rules for all hosts 
		this.resetHostRules();
	}

	/**
	 * Event handler called when a switch leaves the network.
	 * @param DPID for the switch
	 */
	@Override
	public void switchRemoved(long switchId) {
		IOFSwitch sw = this.floodlightProv.getSwitch(switchId);
		log.info(String.format("Switch s%d removed", switchId));
		
		// Change routing rules for all hosts 
		this.resetHostRules();
	}

	/**
	 * Event handler called when multiple links go up or down.
	 * @param updateList information about the change in each link's state
	 */
	@Override
	public void linkDiscoveryUpdate(List<LDUpdate> updateList) {
		for (LDUpdate update : updateList) {
			if (0 == update.getDst()) {
				// If we only know the switch & port for one end of the link, then
				// the link must be from a switch to a host
				log.info(String.format("Link s%s:%d -> host updated", 
					update.getSrc(), update.getSrcPort()));
			} else {
				// Otherwise, the link is between two switches
				log.info(String.format("Link s%s:%d -> s%s:%d updated", 
					update.getSrc(), update.getSrcPort(),
					update.getDst(), update.getDstPort()));
			}
		}
		
		// Change routing rules for all hosts  
		this.resetHostRules();
	}

	/**
	 * Event handler called when link goes up or down.
	 * @param update information about the change in link state
	 */
	@Override
	public void linkDiscoveryUpdate(LDUpdate update) { this.linkDiscoveryUpdate(Arrays.asList(update)); }
	
	/**
     * Event handler called when the IP address of a host changes.
     * @param device information about the host
     */
	@Override
	public void deviceIPV4AddrChanged(IDevice device) { this.deviceAdded(device); }

	/**
     * Event handler called when the VLAN of a host changes.
     * @param device information about the host
     */
	@Override
	public void deviceVlanChanged(IDevice device) { }
	/* Nothing we need to do, since we're not using VLANs */
	
	/**
	 * Event handler called when the controller becomes the master for a switch.
	 * @param DPID for the switch
	 */
	@Override
	public void switchActivated(long switchId) { }
	/* Nothing we need to do, since we're not switching controller roles */

	/**
	 * Event handler called when some attribute of a switch changes.
	 * @param DPID for the switch
	 */
	@Override
	public void switchChanged(long switchId) { }
	/* Nothing we need to do */
	
	/**
	 * Event handler called when a port on a switch goes up or down, or is
	 * added or removed.
	 * @param DPID for the switch
	 * @param port the port on the switch whose status changed
	 * @param type the type of status change (up, down, add, remove)
	 */
	@Override
	public void switchPortChanged(long switchId, ImmutablePort port, PortChangeType type) { }
	/* Nothing we need to do, since we'll get a linkDiscoveryUpdate event */

	/**
	 * Gets a name for this module.
	 * @return name for this module
	 */
	@Override
	public String getName() { return this.MODULE_NAME; }

	/**
	 * Check if events must be passed to another module before this module is
	 * notified of the event.
	 */
	@Override
	public boolean isCallbackOrderingPrereq(String type, String name) { return false; }

	/**
	 * Check if events must be passed to another module after this module has
	 * been notified of the event.
	 */
	@Override
	public boolean isCallbackOrderingPostreq(String type, String name) { return false; }
	
    /**
     * Tell the module system which services we provide.
     */
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() { return null; }

	/**
     * Tell the module system which services we implement.
     */
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() { return null; }

	/**
     * Tell the module system which modules we depend on.
     */
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService >> floodlightService =
	            new ArrayList<Class<? extends IFloodlightService>>();
        floodlightService.add(IFloodlightProviderService.class);
        floodlightService.add(ILinkDiscoveryService.class);
        floodlightService.add(IDeviceService.class);
        return floodlightService;
	}

	
	// ===== Handling of router rules =====
	/**
     * Reset rules on hosts
     */
	private void resetHostRules() {
		for (Host h : this.getHosts()) {
			this.removeSwitchRules(h);
			this.setRouterAndHostRules(h);
		}
	}
	
	/**
     * Set rules on routes and hosts
     */
	private void setRouterAndHostRules(Host h) {
		// Check if host is attached to switch
		if (!h.isAttachedToSwitch()) {
			return;
		}
		
		// Get routes based on Bellman Ford algorithm
		Map<Long, Integer> routeList = getRoutes(h.getSwitch());
		
		// Configure OFMatch variable
		OFMatch routeMatch = new OFMatch();
		routeMatch.setDataLayerType(OFMatch.ETH_TYPE_IPV4);
		routeMatch.setNetworkDestination(h.getIPv4Address());
		
		// Iterate over routes and install rules
		for (Long routeID : routeList.keySet()) {
			OFAction actRoute = new OFActionOutput(routeList.get(routeID));
			//List<OFAction> actRouteList = Arrays.asList(actRoute);
			OFInstruction instrRoute = new OFInstructionApplyActions(Arrays.asList(actRoute));
			//List<OFInstruction> instrRouteList = Arrays.asList(instrRoute);
			
			SwitchCommands.installRule(
				this.getSwitches().get(routeID),
				this.table,
				SwitchCommands.DEFAULT_PRIORITY,
				routeMatch,
				Arrays.asList(instrRoute)
			);
		}
		
		OFAction actHost = new OFActionOutput(h.getPort());
		//List<OFAction> actHostList = Arrays.asList(actHost);
		OFInstruction instrHost = new OFInstructionApplyActions(Arrays.asList(actHost));
		//List<OFInstruction> instrHostList = Arrays.asList(instrHost);
		
		SwitchCommands.installRule(
				h.getSwitch(),
				this.table,
				SwitchCommands.DEFAULT_PRIORITY,
				routeMatch,
				Arrays.asList(instrHost)
		);
	}
	
	/**
     * Remove rules from switches
     */
	private void removeSwitchRules(Host h) {
		for (IOFSwitch switchID : this.getSwitches().values()) {
			// Configure OFMatch variable
			OFMatch routeMatch = new OFMatch();
			routeMatch.setDataLayerType(OFMatch.ETH_TYPE_IPV4);
			routeMatch.setNetworkDestination(h.getIPv4Address());
			
			// Remove rules
			SwitchCommands.removeRules(switchID, this.table, routeMatch);
		}
	}
	
	/**
     * Retrieve routes based on Bellman Ford algorithm
     */
	private Map<Long, Integer> getRoutes(IOFSwitch s) {
		// Variables to keep track of links
		Queue<Long> switchesToVisitQueue = new LinkedList<Long>();
		Map<Long, Integer> destinationSwitch = new ConcurrentHashMap<Long, Integer>();
		Map<Long, Integer> previousSwitch = new ConcurrentHashMap<Long,Integer>();
		
		// Add switches to destination
		for (IOFSwitch switchID : this.getSwitches().values()) {
			destinationSwitch.put(switchID.getId(), Integer.MAX_VALUE);
		}
		
		// Add current switch to destintion
		destinationSwitch.put(s.getId(), 0);
		
		// Iterate over switches
		Collection<Link> linksCollection;
		
		for (int i = 0; i < this.getSwitches().size(); i++) {
			//Iterate over duplicate links and remove them
			linksCollection = new ArrayList<Link>();
			boolean duplicateFlag = false;

	    		for (Link link : this.getLinks()) {
	    			duplicateFlag = false;

	    			for (Link item : linksCollection) {
	    				boolean checkSrcDestOne = (link.getSrc() == item.getSrc() && link.getDst() == item.getDst());
	    				boolean checkSrcDestTwo = (link.getDst() == item.getSrc() && link.getSrc() == item.getDst());
	    			
	    				if (checkSrcDestOne || checkSrcDestTwo) {
	    					duplicateFlag = true;
		    				break;
		    			}
	    			}

		    		if (!duplicateFlag) {
		    			linksCollection.add(link);
		    		}
		   	 }
			
	    		// Iterate over queue
	    		switchesToVisitQueue.add(s.getId());
	    	
			while (!switchesToVisitQueue.isEmpty()) {
				// Get switch from queue
				long currentSwitch = switchesToVisitQueue.remove();
				
				// Create temporary collection of links based on linksCollection
				Collection<Link> tempLinksCollection = new ArrayList<Link>();
				
				for (Link link : linksCollection) {
					if (link.getSrc() == currentSwitch || link.getDst() == currentSwitch) {
						tempLinksCollection.add(link);
					}
				}
				
				// Iterate over items in tempLinksCollection
				for (Link link : tempLinksCollection) {
					int nextLink = Integer.MAX_VALUE;
					int currentDestLink = destinationSwitch.get(currentSwitch);
					
					if (currentSwitch != link.getSrc())  {
						nextLink = destinationSwitch.get(link.getSrc());

						if (nextLink > (currentDestLink + 1))  {
							destinationSwitch.put(link.getSrc(), (currentDestLink + 1));
				    		previousSwitch.put(link.getSrc(), link.getSrcPort());
						}
	    		
						switchesToVisitQueue.add(link.getSrc());
					} else {
						nextLink = destinationSwitch.get(link.getDst());
						
						if (nextLink > (currentDestLink + 1))  {
							destinationSwitch.put(link.getDst(), (currentDestLink + 1));
				    		previousSwitch.put(link.getDst(), link.getDstPort());
						}
						
						switchesToVisitQueue.add(link.getDst());
				}
				linksCollection.remove(link);
	    		}
	    	}
	    }
	
	    return previousSwitch;
	}
}
