package edu.wisc.cs.sdn.apps.loadbalancer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openflow.protocol.instruction.OFInstruction;
import org.openflow.protocol.instruction.OFInstructionApplyActions;
import org.openflow.protocol.instruction.OFInstructionGotoTable;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionSetField;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFOXMFieldType;

import edu.wisc.cs.sdn.apps.util.ArpServer;
import edu.wisc.cs.sdn.apps.util.SwitchCommands;
import edu.wisc.cs.sdn.apps.l3routing.L3Routing;
import edu.wisc.cs.sdn.apps.util.ArpServer;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch.PortChangeType;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.ImmutablePort;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.internal.DeviceManagerImpl;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;

public class LoadBalancer implements IFloodlightModule, IOFSwitchListener,
		IOFMessageListener
{
	public static final String MODULE_NAME = LoadBalancer.class.getSimpleName();

	private static final byte TCP_FLAG_SYN = 0x02;

	private static final short IDLE_TIMEOUT = 20;

	// Interface to the logging system
    private static Logger log = LoggerFactory.getLogger(MODULE_NAME);

    // Interface to Floodlight core for interacting with connected switches
    private IFloodlightProviderService floodlightProv;

    // Interface to device manager service
    private IDeviceService deviceProv;

    // Switch table in which rules should be installed
    private byte table;

    // Set of virtual IPs and the load balancer instances they correspond with
    private Map<Integer,LoadBalancerInstance> instances;

    /**
     * Loads dependencies and initializes data structures.
     */
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException
	{
		log.info(String.format("Initializing %s...", MODULE_NAME));

		// Obtain table number from config
		Map<String,String> config = context.getConfigParams(this);
        this.table = Byte.parseByte(config.get("table"));

        // Create instances from config
        this.instances = new HashMap<Integer,LoadBalancerInstance>();
        String[] instanceConfigs = config.get("instances").split(";");
        for (String instanceConfig : instanceConfigs)
        {
        	String[] configItems = instanceConfig.split(" ");
        	if (configItems.length != 3)
        	{
        		log.error("Ignoring bad instance config: " + instanceConfig);
        		continue;
        	}
        	LoadBalancerInstance instance = new LoadBalancerInstance(
        			configItems[0], configItems[1], configItems[2].split(","));
            this.instances.put(instance.getVirtualIP(), instance);
            log.info("Added load balancer instance: " + instance);
        }

		this.floodlightProv = context.getServiceImpl(
				IFloodlightProviderService.class);
        this.deviceProv = context.getServiceImpl(IDeviceService.class);

	}


	/**
     * Subscribes to events and performs other startup tasks.
     */
	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException
	{
		log.info(String.format("Starting %s...", MODULE_NAME));
		this.floodlightProv.addOFSwitchListener(this);
		this.floodlightProv.addOFMessageListener(OFType.PACKET_IN, this);

	}

	/**
     * Event handler called when a switch joins the network.
     * @param DPID for the switch
     */
	@Override
	public void switchAdded(long switchId)
	{
		IOFSwitch sw = this.floodlightProv.getSwitch(switchId);
		log.info(String.format("Switch s%d added", switchId));

		// Install rules to send
		for(Integer virtualIP: this.instances.keySet())
		{
			// Install rules for IPV4 and TPC
			OFMatch temp1 = new OFMatch();
			OFAction decision1 = new OFActionOutput(OFPort.OFPP_CONTROLLER);
			OFInstruction order1 = new OFInstructionApplyActions(Arrays.asList(decision1));

			temp1.setDataLayerType(OFMatch.ETH_TYPE_IPV4);
			temp1.setNetworkProtocol(OFMatch.IP_PROTO_TCP);
			temp1.setNetworkDestination(virtualIP);

			SwitchCommands.installRule(sw, table, (short)(SwitchCommands.DEFAULT_PRIORITY+1), temp1, Arrays.asList(order1));

			// Install rules for ARP
			OFMatch temp2 = new OFMatch();
                        OFAction decision2 = new OFActionOutput(OFPort.OFPP_CONTROLLER);
                        OFInstruction order2 = new OFInstructionApplyActions(Arrays.asList(decision2));

			temp2.setDataLayerType(OFMatch.ETH_TYPE_ARP);
			temp2.setNetworkDestination(virtualIP);

			SwitchCommands.installRule(sw, table, (short)(SwitchCommands.DEFAULT_PRIORITY + 1), temp2, Arrays.asList(order2));
		}

		OFInstruction rule = new OFInstructionGotoTable(L3Routing.table);
		OFMatch connection = new OFMatch();
		SwitchCommands.installRule(sw, table, SwitchCommands.DEFAULT_PRIORITY, connection, Arrays.asList(rule));
	}

	/**
	 * Handle incoming packets sent from switches.
	 * @param sw switch on which the packet was received
	 * @param msg message from the switch
	 * @param cntx the Floodlight context in which the message should be handled
	 * @return indication whether another module should also process the packet
	 */
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx)
	{
		// We're only interested in packet-in messages
		if (msg.getType() != OFType.PACKET_IN)
		{ return Command.CONTINUE; }
		OFPacketIn pktIn = (OFPacketIn)msg;

		// Handle the packet
		Ethernet ethPkt = new Ethernet();
		ethPkt.deserialize(pktIn.getPacketData(), 0,
				pktIn.getPacketData().length);

		// Send ARP reply for ARP requests for virtual IPs
		if (ethPkt.getEtherType() == Ethernet.TYPE_IPv4) {
			IPv4 ipPacket = (IPv4) ethPkt.getPayload();
                        TCP tranconproPacket = (TCP) ipPacket.getPayload();
                        boolean checkFlagAndTcp = (TCP_FLAG_SYN == tranconproPacket.getFlags() && ipPacket.getProtocol() == IPv4.PROTOCOL_TCP);

                        if (checkFlagAndTcp) {
                                // Install rules for IPV4 and MAC
				int targetIP = ipPacket.getDestinationAddress();
                                int hostNext = this.instances.get(targetIP).getNextHostIP();

                                OFMatch temp1 = new OFMatch();
                                OFAction addressIP1 = new OFActionSetField(OFOXMFieldType.IPV4_DST, hostNext);
                                OFAction mAdrs1 = new OFActionSetField(OFOXMFieldType.ETH_DST, this.getHostMACAddress(hostNext));
                                OFInstruction order1 =  new OFInstructionApplyActions(Arrays.asList(addressIP1, mAdrs1));
                                OFInstruction orderDefault1 = new OFInstructionGotoTable(L3Routing.table);

                                temp1.setDataLayerType(Ethernet.TYPE_IPv4);
                                temp1.setNetworkProtocol(OFMatch.IP_PROTO_TCP);
                                temp1.setNetworkSource(ipPacket.getSourceAddress());
                                temp1.setNetworkDestination(targetIP);
                                temp1.setTransportSource(OFMatch.IP_PROTO_TCP, tranconproPacket.getSourcePort());
                                temp1.setTransportDestination(OFMatch.IP_PROTO_TCP, tranconproPacket.getDestinationPort());

                                SwitchCommands.installRule(sw, table, (short)(SwitchCommands.DEFAULT_PRIORITY + 2), 
                                        temp1, Arrays.asList(order1, orderDefault1), SwitchCommands.NO_TIMEOUT, IDLE_TIMEOUT);


                                // Install rules for IPV4 and Target
                                OFMatch temp2 = new OFMatch();
                                OFAction addressIP2 = new OFActionSetField(OFOXMFieldType.IPV4_SRC, targetIP);
                                OFAction mAdrs2 = new OFActionSetField(OFOXMFieldType.ETH_SRC, instances.get(targetIP).getVirtualMAC());
                                OFInstruction order2 =  new OFInstructionApplyActions(Arrays.asList(addressIP2, mAdrs2));
                                OFInstruction orderDefault2 = new OFInstructionGotoTable(L3Routing.table);

                                temp2.setDataLayerType(Ethernet.TYPE_IPv4);
                                temp2.setNetworkProtocol(OFMatch.IP_PROTO_TCP);
                                temp2.setNetworkSource(hostNext);
                                temp2.setNetworkDestination(ipPacket.getSourceAddress());
                                temp2.setTransportSource(OFMatch.IP_PROTO_TCP, tranconproPacket.getDestinationPort());
                                temp2.setTransportDestination(OFMatch.IP_PROTO_TCP, tranconproPacket.getSourcePort());

                                SwitchCommands.installRule(sw, table, (short)(SwitchCommands.DEFAULT_PRIORITY + 2), 
                                        temp2, Arrays.asList(order2, orderDefault2), SwitchCommands.NO_TIMEOUT, IDLE_TIMEOUT);
                        }
		} else if (ethPkt.getEtherType() == Ethernet.TYPE_ARP) {
			ARP arpPacket = (ARP) ethPkt.getPayload();
			int virtualIP = IPv4.toIPv4Address(arpPacket.getTargetProtocolAddress());

			if (instances.containsKey(virtualIP)){
				byte[] mAdrs = instances.get(virtualIP).getVirtualMAC();
			
				Ethernet ethernet = new Ethernet();
				ethernet.setEtherType(Ethernet.TYPE_ARP);
				ethernet.setDestinationMACAddress(ethPkt.getSourceMACAddress());
				ethernet.setSourceMACAddress(mAdrs);
				
				ARP addrResPro = new ARP();
				addrResPro.setOpCode(ARP.OP_REPLY);
				addrResPro.setProtocolType(arpPacket.getProtocolType());
				addrResPro.setProtocolAddressLength(arpPacket.getProtocolAddressLength());
				addrResPro.setTargetProtocolAddress(arpPacket.getSenderProtocolAddress());
				addrResPro.setSenderProtocolAddress(virtualIP);
				
				addrResPro.setHardwareType(arpPacket.getHardwareType());
				addrResPro.setHardwareAddressLength(arpPacket.getHardwareAddressLength());
				addrResPro.setTargetHardwareAddress(arpPacket.getSenderHardwareAddress());
				addrResPro.setSenderHardwareAddress(mAdrs);
				
				ethernet.setPayload(addrResPro);
				SwitchCommands.sendPacket(sw, (short)pktIn.getInPort(), ethernet);	
			}
		}

		return Command.CONTINUE;
	}

	/**
	 * Returns the MAC address for a host, given the host's IP address.
	 * @param hostIPAddress the host's IP address
	 * @return the hosts's MAC address, null if unknown
	 */
	private byte[] getHostMACAddress(int hostIPAddress)
	{
		Iterator<? extends IDevice> iterator = this.deviceProv.queryDevices(
				null, null, hostIPAddress, null, null);
		if (!iterator.hasNext())
		{ return null; }
		IDevice device = iterator.next();
		return MACAddress.valueOf(device.getMACAddress()).toBytes();
	}

	/**
	 * Event handler called when a switch leaves the network.
	 * @param DPID for the switch
	 */
	@Override
	public void switchRemoved(long switchId) 
	{ /* Nothing we need to do, since the switch is no longer active */ }

	/**
	 * Event handler called when the controller becomes the master for a switch.
	 * @param DPID for the switch
	 */
	@Override
	public void switchActivated(long switchId)
	{ /* Nothing we need to do, since we're not switching controller roles */ }

	/**
	 * Event handler called when a port on a switch goes up or down, or is
	 * added or removed.
	 * @param DPID for the switch
	 * @param port the port on the switch whose status changed
	 * @param type the type of status change (up, down, add, remove)
	 */
	@Override
	public void switchPortChanged(long switchId, ImmutablePort port,
			PortChangeType type) 
	{ /* Nothing we need to do, since load balancer rules are port-agnostic */}

	/**
	 * Event handler called when some attribute of a switch changes.
	 * @param DPID for the switch
	 */
	@Override
	public void switchChanged(long switchId) 
	{ /* Nothing we need to do */ }
	
    /**
     * Tell the module system which services we provide.
     */
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() 
	{ return null; }

	/**
     * Tell the module system which services we implement.
     */
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> 
			getServiceImpls() 
	{ return null; }

	/**
     * Tell the module system which modules we depend on.
     */
	@Override
	public Collection<Class<? extends IFloodlightService>> 
			getModuleDependencies() 
	{
		Collection<Class<? extends IFloodlightService >> floodlightService =
	            new ArrayList<Class<? extends IFloodlightService>>();
        floodlightService.add(IFloodlightProviderService.class);
        floodlightService.add(IDeviceService.class);
        return floodlightService;
	}

	/**
	 * Gets a name for this module.
	 * @return name for this module
	 */
	@Override
	public String getName() 
	{ return MODULE_NAME; }

	/**
	 * Check if events must be passed to another module before this module is
	 * notified of the event.
	 */
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) 
	{
		return (OFType.PACKET_IN == type 
				&& (name.equals(ArpServer.MODULE_NAME) 
					|| name.equals(DeviceManagerImpl.MODULE_NAME))); 
	}

	/**
	 * Check if events must be passed to another module after this module has
	 * been notified of the event.
	 */
	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) 
	{ return false; }
}
