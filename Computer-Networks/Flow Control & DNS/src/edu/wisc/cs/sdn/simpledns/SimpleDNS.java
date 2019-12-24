package edu.wisc.cs.sdn.simpledns;

import edu.wisc.cs.sdn.simpledns.packet.DNS;
import edu.wisc.cs.sdn.simpledns.packet.DNSQuestion;
import edu.wisc.cs.sdn.simpledns.packet.DNSResourceRecord;
import edu.wisc.cs.sdn.simpledns.packet.DNSRdataAddress;
import edu.wisc.cs.sdn.simpledns.packet.DNSRdataName;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleDNS {
	// Ports for sending and receiving packets
	private static final int S_PORT = 53;
	private static final int R_PORT = 8053;

	// List of DNS Types supported
	private static final List<Short> DNS_TYPES = Arrays.asList(DNS.TYPE_A, DNS.TYPE_AAAA,DNS.TYPE_NS, DNS.TYPE_CNAME);

	// Method to manage packet send/receive
	private static void managePackets(String ipAddress) {
		try {
			DatagramPacket dgPacket = new DatagramPacket(new byte[1500], 1500);
			DatagramSocket dgSocket = new DatagramSocket(R_PORT);
			InetAddress iNetAddress = InetAddress.getByName(ipAddress);

			while (true) {
				// Get DNS and socket
				DNS domainNameSystem = DNS.deserialize(dgPacket.getData(), dgPacket.getLength());
				dgSocket.receive(dgPacket);

				// Check Opcode, questions, and DNS type
				if (domainNameSystem.getOpcode() != DNS.OPCODE_STANDARD_QUERY) {
					continue;
				} else if (domainNameSystem.getQuestions().isEmpty()) {
					continue;
				} else if (!DNS_TYPES.contains(domainNameSystem.getQuestions().get(0).getType())) {
					continue;
				}

				// Get response
				DatagramPacket response;

				if (!domainNameSystem.isRecursionDesired()) {
					DatagramSocket newSocket = new DatagramSocket();

					// Send packet
                                        DatagramPacket sendPacket = new DatagramPacket(dgPacket.getData(), dgPacket.getLength(), iNetAddress, S_PORT);
                                        newSocket.send(sendPacket);

					// Receive packet
					DatagramPacket receivedPacket = new DatagramPacket(new byte[1500], 1500);
					newSocket.receive(receivedPacket);

					// Close socket and set response
					newSocket.close();
					response = receivedPacket;
				} else {
					DatagramSocket newSocket = new DatagramSocket();

					// Send packet
					DatagramPacket sendPacket = new DatagramPacket(dgPacket.getData(), dgPacket.getLength(), iNetAddress, S_PORT);
					newSocket.send(sendPacket);

					// Receive packet, Set response, and close socket
					response = recursivePacketResponse(newSocket, sendPacket, domainNameSystem, iNetAddress);
					newSocket.close();
				}

				// Configure response
				response.setAddress(dgPacket.getAddress());
				response.setPort(dgPacket.getPort());

				// Send
				dgSocket.send(response);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static DatagramPacket recursivePacketResponse(DatagramSocket s, DatagramPacket sPacket,
		 	DNS domainNameSystem, InetAddress iNetAddress) throws Exception {
		// Receive packet
		DatagramPacket rPacket = new DatagramPacket(new byte[1500], 1500);

		List<DNSResourceRecord> cNameList = new ArrayList<DNSResourceRecord>();
		List<DNSResourceRecord> dnsAuthorityList = new ArrayList<DNSResourceRecord>();
		List<DNSResourceRecord> dnsAdditionalList = new ArrayList<DNSResourceRecord>();

		while (true) {
			DNS dns = DNS.deserialize(sPacket.getData(), sPacket.getData().length);
			s.receive(sPacket);

			// Set DNS Additionals
			List<DNSResourceRecord> localAdditionals = dns.getAdditional();

			if (localAdditionals.size() > 0) {
				dnsAdditionalList = localAdditionals;
			}

			// Set DNS Authorities
			List<DNSResourceRecord> localAuthorities = dns.getAuthorities();

			if (localAuthorities.size() > 0) {
				for (DNSResourceRecord authority : localAuthorities) {
					if (DNS_TYPES.contains(authority.getType())) {
						dnsAuthorityList = localAuthorities;
						break;
					}
				}
			}

			// Set Answers
			List<DNSResourceRecord> localAnswers = dns.getAnswers();

			if (localAnswers.size() > 0) {
				// Get first answer
				DNSResourceRecord answersList = localAnswers.get(0);

				if (answersList.getType() != DNS.TYPE_CNAME) {
					// Add cnames to answers list
					for (DNSResourceRecord cname : cNameList) {
						localAnswers.add(cname);
					}

					// Check additionals
					if (dns.getAdditional().size() == 0) {
						dns.setAdditional(dnsAdditionalList);
					}

					// Check authorities
					if (dns.getAuthorities().size() == 0) {
						dns.setAuthorities(dnsAuthorityList);
					}

					// Set DNS
					dns.setQuery(false);
					dns.setRecursionDesired(true);
					dns.setAuthenicated(false);
					dns.setAuthoritative(false);
					dns.setAnswers(localAnswers);
					dns.setOpcode((byte) 0);
					dns.setQuestions(dns.getQuestions());
					dns.setRecursionAvailable(true);
					dns.setTruncated(false);
					dns.setRcode((byte) 0);
					dns.setCheckingDisabled(false);

					// Close connection and return packet
					s.close();
					return new DatagramPacket(dns.serialize(), dns.getLength());
				} else {
					// Add to cNameList
					cNameList.add(answersList);

					// Get questions
					List<DNSQuestion> listOfQuestions = new ArrayList<DNSQuestion>();
					listOfQuestions.add(new DNSQuestion(((DNSRdataName) answersList.getData()).getName(), dns.getQuestions().get(0).getType()));

					// Set DNS Request
					DNS dnsRequest = new DNS();
					dnsRequest.setAuthenicated(false);
					dnsRequest.setRecursionDesired(true);
					dnsRequest.setId(domainNameSystem.getId());
					dnsRequest.setQuery(true);
					dnsRequest.setTruncated(false);
					dnsRequest.setOpcode((byte) 0);
					dnsRequest.setQuestions(listOfQuestions);

					// Send packet
					s.send(new DatagramPacket(dnsRequest.serialize(), dnsRequest.getLength(), iNetAddress, S_PORT));
				}
			} else {
				// Set answers if none existing
				InetAddress newIpAddress = null;
				boolean foundAuthority = false;

				// Get newIPAddress
				for (DNSResourceRecord authority : localAuthorities) {
					for (DNSResourceRecord additional : localAdditionals) {
						boolean checkAdditional = DNS.TYPE_A == additional.getType();
						boolean checkAuthority = DNS.TYPE_NS == authority.getType();
						boolean checkName = additional.getName().equals(((DNSRdataName) authority.getData()).getName());

						if (checkAuthority && checkAdditional && checkName) {
							newIpAddress = ((DNSRdataAddress) additional.getData()).getAddress();
							foundAuthority = true;
							break;
						}
					}
					if (foundAuthority) {
						break;
					}
				}

				// Find IP if null and return Datagram
				if (newIpAddress == null) {
					DNS dnsReply = new DNS();

					// Add cnames
					for (DNSResourceRecord cname : cNameList) {
						localAnswers.add(cname);
					}

					// Set answers and questions
					dnsReply.setAnswers(localAnswers);
					dnsReply.setQuestions(domainNameSystem.getQuestions());

					// Filter additionals that match type
					List<DNSResourceRecord> filteredLocalAdditionals = new ArrayList<DNSResourceRecord>();

					for (DNSResourceRecord additional : localAdditionals) {
						if (DNS_TYPES.contains(additional.getType())) {
							filteredLocalAdditionals.add(additional);
						}
					}

					localAdditionals = filteredLocalAdditionals;

					if (localAdditionals.size() == 0) {
                                                localAdditionals = dnsAdditionalList;
                                        }

					// Filter authorities that match type
					List<DNSResourceRecord> filteredLocalAuthorities = new ArrayList<DNSResourceRecord>();

					for (DNSResourceRecord authority : localAuthorities) {
						if (DNS_TYPES.contains(authority.getType())) {
							filteredLocalAuthorities.add(authority);
						}
					}

					localAuthorities = filteredLocalAuthorities;

					if (localAuthorities.size() == 0) {
						localAuthorities = dnsAuthorityList;
					}

					// Set reply
					dnsReply.setQuery(false);
					dnsReply.setRecursionDesired(true);
					dnsReply.setAuthenicated(false);
					dnsReply.setAuthoritative(false);
					dnsReply.setOpcode((byte) 0);
					dnsReply.setRecursionAvailable(true);
					dnsReply.setTruncated(false);
					dnsReply.setRcode((byte) 0);
					dnsReply.setCheckingDisabled(false);
					dnsReply.setAuthorities(localAuthorities);
					dnsReply.setAdditional(localAdditionals);
					dnsReply.setId(domainNameSystem.getId());

					// Close connection and send packet
					s.close();
					return new DatagramPacket(dnsReply.serialize(), dnsReply.getLength());
				}

				// Send packet
				s.send(new DatagramPacket(rPacket.getData(), rPacket.getLength(), newIpAddress, S_PORT));
			}
		}
	}

	private static void callError() {
		System.out.println("Format error. Please include -r <root server ip> and -e <ec2 csv> on your call.");
		System.exit(0);
	}

	public static void main(String[] args) {
		if (args.length != 4 || !args[0].equals("-r") || !args[2].equals("-e")){
			callError();
		} else if (!args[0].equals("-r")) {
			callError();
		} else if (!args[2].equals("-e")) {
			callError();
		}

		managePackets(args[1]);
	}
}
