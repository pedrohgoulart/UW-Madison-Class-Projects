package edu.wisc.cs.sdn.vnet.rt;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


import net.floodlightcontroller.packet.*;


import edu.wisc.cs.sdn.vnet.Device;
import edu.wisc.cs.sdn.vnet.DumpFile;
import edu.wisc.cs.sdn.vnet.Iface;

/**
 * @author Aaron Gember-Jacobson and Anubhavnidhi Abhashkumar
 */
public class Router extends Device {
	/** Routing table for the router */
	private RouteTable routeTable;

	/** ARP cache for the router */
	private ArpCache arpCache;

	// Keeps track of ARP requests
	private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<IPacket>> arpQueue;
	private ConcurrentHashMap<Integer, Integer> arpCount;


	private Timer t;
	private Timer t1;
	boolean loadedRouteTable;
	

	/**
	 * Creates a router for a specific host.
	 * @param host hostname for the router
	 */
	public Router(String host, DumpFile logfile) {
		super(host,logfile);
		this.routeTable = new RouteTable();
		this.arpCache = new ArpCache();
		this.arpQueue = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<IPacket>>();
		this.arpCount = new ConcurrentHashMap<Integer, Integer>();
		
	}

	/**
	 * @return routing table for the router
	 */
	public RouteTable getRouteTable() { return this.routeTable; }

	
	class check extends TimerTask {
		
		public void run() 
		{
			System.out.println("======= Broadcast Initiated in polling =======");
			polling();
			System.out.println("======= Broadcast Completed in polling =======");
			
		}
	}
	
	class check1 extends TimerTask 
	{
		
		public void run() 
		{
			System.out.println("======= Check on route table entries initiated =======");
			update_routeTable();
			System.out.println("======= Check on route table entries completed =======");
		}
	}
	
	
	public void polling() 
	{
		for (String temp : this.interfaces.keySet()) {
			riprequestresponsesender(this.interfaces.get(temp), true, false);
		}
	}
	
	public void update_routeTable() 
	{
		for(RouteEntry re : this.routeTable.getEntries())
		{
			if(re.getTime() != 0 && re.getGatewayAddress() != 0) 
			{
				
				if((System.currentTimeMillis() - re.getTime()) > 30000) {
					System.out.println("======= Removed entry for 30 sec timeout =======");
				     this.routeTable.remove(re.getDestinationAddress(), re.getMaskAddress());
				}
			}
		}
	}
	 
	public void setRip() {
		System.out.println("======= Initialize Route Table for RIP =======");
		for(Iface ifc : this.interfaces.values()) {
			int mask = ifc.getSubnetMask();
			int destination = ifc.getIpAddress() & mask;
			this.routeTable.insert(destination, 0, mask, ifc, 1);
		}
		
		for (Iface ifcs : this.interfaces.values()) {
			this.riprequestresponsesender(ifcs, true, true);
		}
		
		this.t1 = new Timer();
		this.t = new Timer();
		this.t1.scheduleAtFixedRate(new check1(), 1000, 1000);
		this.t.scheduleAtFixedRate(new check(), 10000, 10000);		
	}

	private void riprequestresponsesender(Iface inIface, boolean multicast, boolean request) 
	{
		System.out.println("======= Initiated Response/Request =======");

		IPv4 ip = new IPv4();
		Ethernet ether = new Ethernet();
		RIPv2 packetRIP = new RIPv2();
		UDP packetUDP = new UDP();
		ether.setEtherType(Ethernet.TYPE_IPv4);
		ether.setPayload(ip);
		ether.setSourceMACAddress("FF:FF:FF:FF:FF:FF");
		ip.setPayload(packetUDP);
		packetUDP.setPayload(packetRIP);
		
		ip.setVersion((byte)4);
		ip.setTtl((byte)64);
		ip.setProtocol(IPv4.PROTOCOL_UDP);
		
		if(!multicast) 
		{
			ether.setDestinationMACAddress(inIface.getMacAddress().toBytes());	
			ip.setDestinationAddress(inIface.getIpAddress());
		}
		else 
		{
			ether.setDestinationMACAddress("FF:FF:FF:FF:FF:FF");
			ip.setDestinationAddress("224.0.0.9");
		}
		
		
		packetUDP.setSourcePort(UDP.RIP_PORT);
		packetUDP.setDestinationPort(UDP.RIP_PORT);
		
		if(request) 
		{
			packetRIP.setCommand(RIPv2.COMMAND_REQUEST);
		}
		else 
		{
			packetRIP.setCommand(RIPv2.COMMAND_RESPONSE);
		}
		
		for(RouteEntry ent : this.routeTable.getEntries()) {
			int addr = ent.getDestinationAddress();
			int mt = ent.getdvMetric();
			int msk = ent.getMaskAddress();
			int ipaddress = inIface.getIpAddress();
			
			RIPv2Entry RIPent = new RIPv2Entry(addr,msk, mt);
			RIPent.setNextHopAddress(ipaddress);
			packetRIP.addEntry(RIPent);
			
		}
		
		ether.serialize();
		this.sendPacket(ether, inIface);
		System.out.println("======= Completed and Sent Out Response/Request =======");
		
	}
	
	public void riprequestresponseHandler(Ethernet ether, Iface inIface) 
	{
		System.out.println("======= Initiated Response/Request Handler =======");
		IPv4 ipaddress = (IPv4)ether.getPayload();
		UDP data = (UDP)ipaddress.getPayload();
		RIPv2 rip = (RIPv2)data.getPayload();
		
		boolean checkMAC = (ether.getDestinationMAC().toLong() == MACAddress.valueOf("FF:FF:FF:FF:FF:FF").toLong() ? true : false);
		boolean checkIP = (ipaddress.getDestinationAddress() == IPv4.toIPv4Address("224.0.0.9") ? true : false);
		
		//Check and deal with broadcast request
		if (rip.getCommand() == RIPv2.COMMAND_REQUEST && checkMAC && checkIP) {
			System.out.println("======= Response to Initial Broadast request =======");
			riprequestresponsesender(inIface, true, false);
			return;
		}
		
			
		for(RIPv2Entry ripEnt: rip.getEntries()) 
		{
			int addr = ripEnt.getAddress();
			int distance = ripEnt.getMetric() + 1;
			int hop = ripEnt.getNextHopAddress();
			int msk = ripEnt.getSubnetMask();
			    
			RouteEntry ent = this.routeTable.lookup(addr);
			boolean need_to_rem = false;
				
			if(ent != null) 
			{
				if(ent.getGatewayAddress() != 0 && ent.getdvMetric() == distance) 
				{
					need_to_rem = true;
					
				}
			}	
			if(ent == null) 
			{
				System.out.println("======= Inserted entry into Route Table =======");
				this.routeTable.insert(addr, hop, msk, inIface, distance);
				this.riprequestresponsesender(inIface, false, false);
			}
			else if(ent.getdvMetric() > distance) 
			{
				System.out.println("======= Updated Route Table entry =======");
				this.routeTable.update(addr, msk, hop, inIface, distance); 
				this.riprequestresponsesender(inIface, false, false);
			}
			else if(need_to_rem) 
			{
				ent.setTime(System.currentTimeMillis());
			}
				
		}

	}
			
				
	

	
	/**
	 * Load a new routing table from a file.
	 * @param routeTableFile the name of the file containing the routing table
	 */
	public void loadRouteTable(String routeTableFile) {
		if (!routeTable.load(routeTableFile, this)) {
			System.err.println("Error setting up routing table from file "
					+ routeTableFile);
			System.exit(1);
		}

		System.out.println("Loaded static route table");
		System.out.println("-------------------------------------------------");
		System.out.print(this.routeTable.toString());
		System.out.println("-------------------------------------------------");

		this.loadedRouteTable = true;
	}

	/**
	 * Load a new ARP cache from a file.
	 * @param arpCacheFile the name of the file containing the ARP cache
	 */
	public void loadArpCache(String arpCacheFile) {
		if (!arpCache.load(arpCacheFile)) {
			System.err.println("Error setting up ARP cache from file "
					+ arpCacheFile);
			System.exit(1);
		}

		System.out.println("Loaded static ARP cache");
		System.out.println("----------------------------------");
		System.out.print(this.arpCache.toString());
		System.out.println("----------------------------------");
	}

	  
	
	/**
	 * Handle an Ethernet packet received on a specific interface.
	 * @param etherPacket the Ethernet packet that was received
	 * @param inIface the interface on which the packet was received
	 */
	public void handlePacket(Ethernet etherPacket, Iface inIface) {
		System.out.println("*** -> Received packet: " +
                etherPacket.toString().replace("\n", "\n\t"));

		if (!this.loadedRouteTable && etherPacket.getEtherType() == Ethernet.TYPE_IPv4) {
			IPv4 ipPacket = (IPv4)etherPacket.getPayload();
			int ipPacketDest = ipPacket.getDestinationAddress();

			if (ipPacketDest == inIface.getIpAddress() || ipPacketDest == IPv4.toIPv4Address("224.0.0.9")) {
				if (ipPacket.getProtocol() == IPv4.PROTOCOL_UDP) {
					UDP udp = (UDP) ipPacket.getPayload();
					if (udp.getDestinationPort() == 520) {
						riprequestresponseHandler(etherPacket, inIface);
						return;
					}
				}
			}
		}

		switch(etherPacket.getEtherType()) {
			case Ethernet.TYPE_IPv4:
				this.handleIpPacket(etherPacket, inIface);
				break;
			case Ethernet.TYPE_ARP:
				this.handleArpPacket(etherPacket, inIface);
				break;
		}

		createARPrequest(etherPacket);
	}

	
    private void handleIpPacket(Ethernet etherPacket, Iface inIface) {
	// Make sure it's an IP packet
	if (etherPacket.getEtherType() != Ethernet.TYPE_IPv4) { return; }

	// Get IP header
	IPv4 ipPacket = (IPv4)etherPacket.getPayload();
        System.out.println("Handle IP packet");

        // Verify checksum
        short origCksum = ipPacket.getChecksum();
        ipPacket.resetChecksum();
        byte[] serialized = ipPacket.serialize();
        ipPacket.deserialize(serialized, 0, serialized.length);
        short calcCksum = ipPacket.getChecksum();
        if (origCksum != calcCksum) { return; }

        // Check TTL
        ipPacket.setTtl((byte)(ipPacket.getTtl()-1));
        if (0 == ipPacket.getTtl()) {
		icmpMessage(11, 0, etherPacket, inIface, false);
		return;
	}

        // Reset checksum now that TTL is decremented
        ipPacket.resetChecksum();

        // Check if packet is destined for one of router's interfaces
        for (Iface iface : this.interfaces.values()) {
        	if (ipPacket.getDestinationAddress() == iface.getIpAddress()) {
			// Check IP header
       		 	if (ipPacket.getProtocol() == IPv4.PROTOCOL_UDP || ipPacket.getProtocol() == IPv4.PROTOCOL_TCP) {
				icmpMessage(3, 3, etherPacket, inIface, false);
			} else if (ipPacket.getProtocol() == IPv4.PROTOCOL_ICMP) {
				if (((ICMP) ipPacket.getPayload()).getIcmpType() == 8) {
             		        	icmpMessage(0, 0, etherPacket, inIface, true);
                		}
        		}
			return;
		}
        }

        // Do route lookup and forward
        this.forwardIpPacket(etherPacket, inIface);
    }

    private void forwardIpPacket(Ethernet etherPacket, Iface inIface) {
	// Make sure it's an IP packet
	if (etherPacket.getEtherType() != Ethernet.TYPE_IPv4) { return; }
        System.out.println("Forward IP packet");

	// Get IP header
	IPv4 ipPacket = (IPv4)etherPacket.getPayload();
        int dstAddr = ipPacket.getDestinationAddress();

        // Find matching route table entry
        RouteEntry bestMatch = this.routeTable.lookup(dstAddr);

        // If no entry matched, send ICMP
        if (null == bestMatch) {
		icmpMessage(3, 0, etherPacket, inIface, false);
		return;
	}

        // Make sure we don't sent a packet back out the interface it came in
        Iface outIface = bestMatch.getInterface();
        if (outIface == inIface) { return; }

        // Set source MAC address in Ethernet header
        etherPacket.setSourceMACAddress(outIface.getMacAddress().toBytes());

        // If no gateway, then nextHop is IP destination
        int nextHop = bestMatch.getGatewayAddress();
        if (0 == nextHop) { nextHop = dstAddr; }

        // Set destination MAC address in Ethernet header
        ArpEntry arpEntry = this.arpCache.lookup(nextHop);
        if (arpEntry == null) {
		if (arpQueue.containsKey(nextHop)) {
                        arpQueue.get(nextHop).add(etherPacket);
                } else {
                        ConcurrentLinkedQueue<IPacket> newPacketQueue = new ConcurrentLinkedQueue<IPacket>();
                        newPacketQueue.add(etherPacket);
                        arpQueue.put(nextHop, newPacketQueue);
                        arpCount.put(nextHop, 0);
                }
		return;
	}
        etherPacket.setDestinationMACAddress(arpEntry.getMac().toBytes());

        this.sendPacket(etherPacket, outIface);
    }

    private void handleArpPacket(Ethernet etherPacket, Iface inIface) {
		ARP arpPack = (ARP)etherPacket.getPayload();
		int targIp = ByteBuffer.wrap(arpPack.getTargetProtocolAddress()).getInt();
		int sendIp = ByteBuffer.wrap(arpPack.getSenderProtocolAddress()).getInt();
		if(arpPack.getOpCode() == ARP.OP_REQUEST) 
		{
			if (targIp != inIface.getIpAddress()) { return; }
			Arprequestreply(etherPacket, arpPack, inIface, sendIp);     
		}
		else if(arpPack.getOpCode() == ARP.OP_REPLY)
		{
			MACAddress mac = new MACAddress(arpPack.getSenderHardwareAddress());
			receiveARPreply(arpPack, sendIp, inIface, mac);
		}
		else
		{
			return;
		}
	
    }
    
	private void receiveARPreply(ARP arp, int ip, Iface inpIface, MACAddress mac) 
    {
	   arpCache.insert(mac, ip);
	
	   if(arpQueue.containsKey(ip))
	   {
		   for(IPacket pack : arpQueue.get(ip))
		   {
			  Ethernet etherHeader = (Ethernet) pack;
			  etherHeader.setDestinationMACAddress(mac.toBytes());
			  this.sendPacket((Ethernet) pack, inpIface);
		   }
		
		   arpQueue.remove(ip);
		   arpCount.remove(ip);
		   return;
	   }
	   
	   return;
}


    private void Arprequestreply(Ethernet ether, ARP arp, Iface inpIface, int ip)
    {
    	ARP arpHeader = new ARP();
    	Ethernet etherHeader = new Ethernet();
    	
    	ether.setEtherType(Ethernet.TYPE_ARP);
    	ether.setSourceMACAddress(inpIface.getMacAddress().toBytes());
    	ether.setDestinationMACAddress(ether.getSourceMACAddress());
    	
    	
    	arpHeader.setHardwareType(ARP.HW_TYPE_ETHERNET);
    	arpHeader.setProtocolType(ARP.PROTO_TYPE_IP);
    	arpHeader.setHardwareAddressLength((byte) Ethernet.DATALAYER_ADDRESS_LENGTH);
    	arpHeader.setProtocolAddressLength((byte) 4);
    	arpHeader.setOpCode(ARP.OP_REPLY);
    	
    	arpHeader.setSenderHardwareAddress(inpIface.getMacAddress().toBytes());
    	arpHeader.setSenderProtocolAddress(inpIface.getIpAddress());
    	arpHeader.setTargetHardwareAddress(arp.getSenderHardwareAddress());
    	arpHeader.setTargetProtocolAddress(ip);
    	
    	ether.setPayload(arpHeader);
    	
    	this.sendPacket(etherHeader, inpIface);
    	
    }
    
    private void createARPrequestsub(int address, Iface inpIface) 
    {
    	if(arpCount.get(address).intValue() > 3) 
    	{
    		return;
    	}
    	
    	Ethernet etherHeader = new Ethernet();
    	ARP arpHeader = new ARP();
        
    	etherHeader.setSourceMACAddress(inpIface.getMacAddress().toBytes());
    	etherHeader.setDestinationMACAddress("FF:FF:FF:FF:FF:FF");
	    etherHeader.setEtherType(Ethernet.TYPE_ARP);
	    
	    arpHeader.setHardwareType(ARP.HW_TYPE_ETHERNET);
    	arpHeader.setProtocolType(ARP.PROTO_TYPE_IP);
    	arpHeader.setHardwareAddressLength((byte) Ethernet.DATALAYER_ADDRESS_LENGTH);
    	arpHeader.setProtocolAddressLength((byte) 4);
    	arpHeader.setOpCode(ARP.OP_REQUEST);
    	arpHeader.setSenderHardwareAddress(inpIface.getMacAddress().toBytes());
    	arpHeader.setSenderProtocolAddress(inpIface.getIpAddress());
    	ByteBuffer b = ByteBuffer.allocate(Ethernet.DATALAYER_ADDRESS_LENGTH);
    	b.putInt(0);
    	arpHeader.setTargetHardwareAddress(b.array());
    	arpHeader.setTargetProtocolAddress(address);
    	
    	etherHeader.setPayload(arpHeader);
    	
    	this.sendPacket(etherHeader, inpIface);
    	arpCount.put(address, (arpCount.get(address).intValue() + 1));
    }
 
    
    private void createARPrequest(Ethernet etherHeader) 
    {
    	Iterator<Entry<Integer, ConcurrentLinkedQueue<IPacket>>> iterator = arpQueue.entrySet().iterator();
    	
    	while(iterator.hasNext()) 
    	{
    		Entry<Integer, ConcurrentLinkedQueue<IPacket>> entry = iterator.next();
    		Queue<IPacket> q = entry.getValue();
    		int ip = entry.getKey();
    		RouteEntry rte = this.routeTable.lookup(ip);
    		
    		if(arpCount.get(ip).intValue() <= 3) 
    		{
    			createARPrequestsub(ip, rte.getInterface());
    		}
    		else 
    		{
    			Ethernet e = (Ethernet) q.peek();
    			 if (e != null) 
    			 {
     				this.icmpMessage(3, 1, etherHeader, rte.getInterface(), false);
     		     }
    			 arpCount.remove(ip);
 		         iterator.remove();
    		}
    		
    		try {
    			Thread.sleep(1000);
    		} catch(InterruptedException e) {
    		}
    		
    	}
    }
    
    


    private void icmpMessage(int type, int code, Ethernet origPacket, Iface inIface, boolean echo) {
	// Initialize components
	Ethernet ether = new Ethernet();
	IPv4 ip = new IPv4();
	ICMP icmp = new ICMP();
	Data data = new Data();

	// Set Ethernet
        IPv4 origIpPacket = (IPv4) origPacket.getPayload();
        if (origIpPacket == null) { return; }

	ether.setEtherType(Ethernet.TYPE_IPv4);
        ether.setSourceMACAddress(inIface.getMacAddress().toBytes());

	int srcAddr = origIpPacket.getSourceAddress();

        RouteEntry srcAddrEntry = this.routeTable.lookup(srcAddr);
        if (srcAddrEntry == null) { return; }

        int nextAddr = srcAddrEntry.getGatewayAddress();
        if (nextAddr == 0) { nextAddr = srcAddr; }

        ArpEntry cache = this.arpCache.lookup(nextAddr);
	if (cache == null) {
		if (echo) {
			// Return because MAC address was not found
			return;
		}

		if (arpQueue.containsKey(nextAddr)) {
			arpQueue.get(nextAddr).add(ether);
		} else {
			ConcurrentLinkedQueue<IPacket> newPacketQueue = new ConcurrentLinkedQueue<IPacket>();
    			newPacketQueue.add(ether);
    			arpQueue.put(nextAddr, newPacketQueue);
    			arpCount.put(nextAddr, 0);
		}

		return;
        }

        ether.setDestinationMACAddress(cache.getMac().toBytes());

	// Set IP
	ip.setTtl((byte) 64);
        ip.setProtocol(IPv4.PROTOCOL_ICMP);
        ip.setSourceAddress(inIface.getIpAddress());
        ip.setDestinationAddress(origIpPacket.getSourceAddress());

	// Set ICMP
	icmp.setIcmpCode((byte) code);
	icmp.setIcmpType((byte) type);

	// Set data
	if (!echo) {
		ByteArrayOutputStream header = new ByteArrayOutputStream();
                byte[] bytes = {0, 0, 0, 0};

                try {
                       	header.write(bytes);
                       	header.write(origIpPacket.serialize());
                       	byte[] ipBytes = origIpPacket.getPayload().serialize();
                       	header.write(Arrays.copyOf(ipBytes, 8));
                } catch (IOException e) {
        	}

		data.setData(header.toByteArray());
	} else {
		data.setData(((ICMP) origIpPacket.getPayload()).getPayload().serialize());
	}

	// Set payload
	ether.setPayload(ip);
	ip.setPayload(icmp);
	icmp.setPayload(data);

	super.sendPacket(ether, inIface);
    }

 
}
