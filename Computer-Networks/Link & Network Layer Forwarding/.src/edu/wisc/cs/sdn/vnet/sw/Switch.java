package edu.wisc.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.MACAddress;
import edu.wisc.cs.sdn.vnet.Device;
import edu.wisc.cs.sdn.vnet.DumpFile;
import edu.wisc.cs.sdn.vnet.Iface;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

/**
 * @author Aaron Gember-Jacobson
 */
class ForwardTableItem {
	long startTime;
	Iface ifaceName;

	public ForwardTableItem(long time, Iface iface) {
		this.startTime = time;
		this.ifaceName = iface;
	}

	public long getTime(){
		return this.startTime;
	}

	public void setTime(long time) {
                this.startTime = time;
        }

	public Iface getIface() {
		return this.ifaceName;
	}
}

public class Switch extends Device {
	// Keeps track of forward table
	public static ConcurrentHashMap<MACAddress, ForwardTableItem> fTableMap = new ConcurrentHashMap<MACAddress, ForwardTableItem>();

	// Timeout for entries in the forward table
	private long _timeout = 15000;

	/**
	 * Creates a router for a specific host.
	 * @param host hostname for the router
	 */
	public Switch(String host, DumpFile logfile) {
		super(host,logfile);
	}

	/**
	 * Handle an Ethernet packet received on a specific interface.
	 * @param etherPacket the Ethernet packet that was received
	 * @param inIface the interface on which the packet was received
	 */
	public void handlePacket (Ethernet etherPacket, Iface inIface) {
	    synchronized(this.fTableMap) {
		// Output packet
		System.out.println("*** -> Received packet: " +
			etherPacket.toString().replace("\n", "\n\t"));

		// Get Mac addresses and check if null
		MACAddress srcMac = etherPacket.getSourceMAC();
		MACAddress destMac = etherPacket.getDestinationMAC();

		if ((srcMac == null) || (destMac == null)) {
			System.out.println("Error: source/destination mac address is Null");
			System.exit(1);
		}

		// Map interfaces and remove entries based on timeout
		Map<String,Iface> packetOutIfaces = getInterfaces();

		for (MACAddress mac : fTableMap.keySet()) {
			if (fTableMap.get(mac) != null) {
				long currTime = Math.abs(System.currentTimeMillis() - fTableMap.get(mac).getTime());
				if (currTime > _timeout) {
					fTableMap.remove(mac);
				}
			}
		}

		// Check if source mac is on map and update/add it
		ForwardTableItem fTableSrc = fTableMap.get(srcMac);
		if (fTableSrc != null) {
			fTableSrc.setTime(System.currentTimeMillis());
		} else {
			ForwardTableItem item = new ForwardTableItem(System.currentTimeMillis(), inIface);
			fTableMap.put(srcMac, item);
		}

		// Check if destination mac is on map and choose where to send
		ForwardTableItem fTableDest = fTableMap.get(destMac);
		if (fTableDest != null) {
			// Check if destination is reached
			if (fTableDest.getIface() == inIface) {
				System.exit(1);
			} else {
				// Forward packet to interface
				sendPacket(etherPacket, fTableDest.getIface());
			}
		} else {
			// Broadcast packet
			for (String mac : packetOutIfaces.keySet()) {
				if (!mac.equals(inIface.getName())) {
					sendPacket(etherPacket, packetOutIfaces.get(mac));
				}
			}
		}
	    }
	}
}
