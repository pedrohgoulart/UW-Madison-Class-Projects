package edu.wisc.cs.sdn.vnet.rt;

import edu.wisc.cs.sdn.vnet.Device;
import edu.wisc.cs.sdn.vnet.DumpFile;
import edu.wisc.cs.sdn.vnet.Iface;

import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author Aaron Gember-Jacobson and Anubhavnidhi Abhashkumar
 */
public class Router extends Device {
	/** Routing table for the router */
	private RouteTable routeTable;

	/** ARP cache for the router */
	private ArpCache arpCache;

	/**
	 * Creates a router for a specific host.
	 * @param host hostname for the router
	 */
	public Router(String host, DumpFile logfile) {
		super(host,logfile);
		this.routeTable = new RouteTable();
		this.arpCache = new ArpCache();
	}

	/**
	 * @return routing table for the router
	 */
	public RouteTable getRouteTable() { return this.routeTable; }

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

		if (etherPacket.getEtherType() == Ethernet.TYPE_IPv4) {
			// Get IP header and convert to IPv4
			IPv4 pHeader = (IPv4) etherPacket.getPayload();

			// Set checksum from packet header
			pHeader.setChecksum( (short) 0);

			// Sets up buffer
			byte hLength = pHeader.getHeaderLength();
			int oLength = 0;

			if (pHeader.getOptions() != null) {
				oLength = (pHeader.getOptions().length / 4);
				hLength = (byte) (hLength + oLength);
			}

			byte[] byteData = new byte[(pHeader.getHeaderLength() * 4)];
			ByteBuffer buffer = ByteBuffer.wrap(byteData);

			// Fill buffer
			buffer.put( (byte) (((pHeader.getVersion() & 0xf) << 4) | (hLength & 0xf)));
			buffer.put(pHeader.getDiffServ());
			buffer.putShort( (short) pHeader.getTotalLength());
			buffer.putShort(pHeader.getIdentification());
			buffer.putShort( (short) (((pHeader.getFlags() & 0x7) << 13) | (pHeader.getFragmentOffset() & 0x1fff)));
			buffer.put(pHeader.getTtl());
			buffer.put(pHeader.getProtocol());
			buffer.putShort(pHeader.getChecksum());
			buffer.putInt(pHeader.getSourceAddress());
			buffer.putInt(pHeader.getDestinationAddress());

			if (pHeader.getOptions() != null) {
				buffer.put(pHeader.getOptions());
			}

			buffer.rewind();

			// Compute checksum
			int tempChecksum = 0;

			for (int i = 0; i < hLength * 2; i++) {
				tempChecksum += 0xffff & buffer.getShort();
			}

			tempChecksum = ((tempChecksum >> 16) &0xffff) + (tempChecksum & 0xffff);

			short newChecksum = (short) (~tempChecksum & 0xffff);

			// Check if checksum is different than pHeader checksum
			if (pHeader.getChecksum() == (newChecksum + 0)) {
				pHeader.setChecksum(newChecksum);
				pHeader.setTtl( (byte) (pHeader.getTtl() - 1));

				// Check TTL and resend
				if (pHeader.getTtl() > 0) {
					Map<String,Iface> packetOutIfaces = getInterfaces();
					boolean checkIPMatch = false;

					for (String mac : packetOutIfaces.keySet()) {
						if (pHeader.getDestinationAddress() == packetOutIfaces.get(mac).getIpAddress()) {
							checkIPMatch = true;
							break;
						}
					}

					if (!checkIPMatch) {
						sendPackets(pHeader, etherPacket, inIface);
					}
				}
			}
		}
	}

	private void sendPackets(IPv4 pHeader, Ethernet etherPacket, Iface inIface) {
		RouteEntry destination;
		destination = routeTable.lookup(pHeader.getDestinationAddress());

		if (destination != null) {
			// Set next hop and arp variables
			String nextHopMac = null				;
			ArpEntry arp = null;

			// Check gateway address
			if (destination.getGatewayAddress() == 0) {
				arp = arpCache.lookup(pHeader.getDestinationAddress());
			} else {
				arp = arpCache.lookup(destination.getGatewayAddress());
			}

			if (arp != null) {
				nextHopMac = arp.getMac().toString();
			}

			// Check next hop
			if (nextHopMac != null) {
				int newSourceIP = destination.getInterface().getIpAddress();
				etherPacket.setDestinationMACAddress(nextHopMac);

				if (inIface == destination.getInterface()) return;

				String setSourceIP = arpCache.lookup(newSourceIP).getMac().toString();

				if (setSourceIP == null) return;

				etherPacket.setSourceMACAddress(setSourceIP);
				pHeader.resetChecksum();

				if (destination.getInterface() != null) {
					sendPacket(etherPacket, destination.getInterface());
				}
			}
		}
	}
}
