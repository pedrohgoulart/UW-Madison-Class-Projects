import enum
import logging
import llp
import queue
import struct
import threading

class SWPType(enum.IntEnum):
    DATA = ord('D')
    ACK = ord('A')

class SWPPacket:
    _PACK_FORMAT = '!BI'
    _HEADER_SIZE = struct.calcsize(_PACK_FORMAT)
    MAX_DATA_SIZE = 1400 # Leaves plenty of space for IP + UDP + SWP header 

    def __init__(self, type, seq_num, data=b''):
        self._type = type
        self._seq_num = seq_num
        self._data = data

    @property
    def type(self):
        return self._type

    @property
    def seq_num(self):
        return self._seq_num
    
    @property
    def data(self):
        return self._data

    def to_bytes(self):
        header = struct.pack(SWPPacket._PACK_FORMAT, self._type.value, 
                self._seq_num)
        return header + self._data
       
    @classmethod
    def from_bytes(cls, raw):
        header = struct.unpack(SWPPacket._PACK_FORMAT,
                raw[:SWPPacket._HEADER_SIZE])
        type = SWPType(header[0])
        seq_num = header[1]
        data = raw[SWPPacket._HEADER_SIZE:]
        return SWPPacket(type, seq_num, data)

    def __str__(self):
        return "%s %d %s" % (self._type.name, self._seq_num, repr(self._data))

class SWPSender:
    _SEND_WINDOW_SIZE = 5
    _TIMEOUT = 1

    def __init__(self, remote_address, loss_probability=0):
        self._llp_endpoint = llp.LLPEndpoint(remote_address=remote_address,
                loss_probability=loss_probability)

        # Start receive thread
        self._recv_thread = threading.Thread(target=self._recv)
        self._recv_thread.start()

        # Additional state variables
        self.lastACKrcvd = 0
        self.sequence_num = 1
        self.buffer = [None] * SWPSender._SEND_WINDOW_SIZE
        self.semaphore = threading.BoundedSemaphore(value = SWPSender._SEND_WINDOW_SIZE)
        self.mutex = threading.BoundedSemaphore(value = 1)
        
    def send(self, data):
        if (data is None):
            return
        
        for i in range(0, len(data), SWPPacket.MAX_DATA_SIZE):
            self._send(data[i:i+SWPPacket.MAX_DATA_SIZE])

    def _send(self, data):
        self.semaphore.acquire()
        
        self.mutex.acquire()

        packet = SWPPacket(SWPType.DATA, self.sequence_num,  data)
        temp_list = [packet, threading.Timer(SWPSender._TIMEOUT, self._retransmit, [self.sequence_num])]
        self.buffer[self.sequence_num % SWPSender._SEND_WINDOW_SIZE] = temp_list
        self.buffer[self.sequence_num % SWPSender._SEND_WINDOW_SIZE][1].start()        
        self.sequence_num = self.sequence_num + 1

        self.mutex.release()

        self._llp_endpoint.send(packet.to_bytes())
        
        return
        
    def _retransmit(self, seq_num):
        if self.buffer[seq_num % SWPSender._SEND_WINDOW_SIZE] is not None:
            # Reset timer
            self.buffer[seq_num % SWPSender._SEND_WINDOW_SIZE][1].cancel()
            self.buffer[seq_num % SWPSender._SEND_WINDOW_SIZE][1] = threading.Timer(SWPSender._TIMEOUT, self._retransmit, [seq_num])
            self.buffer[seq_num % SWPSender._SEND_WINDOW_SIZE][1].start()
            # Resend packet
            packet = self.buffer[seq_num % SWPSender._SEND_WINDOW_SIZE][0]
            self._llp_endpoint.send(packet.to_bytes())

        return 

    def _recv(self):
        while True:
            # Receive SWP packet
            raw = self._llp_endpoint.recv()
            if raw is None:
                continue

            packet = SWPPacket.from_bytes(raw)

            # Check packet and update buffer
            if (packet.type == SWPType.ACK and packet.seq_num > self.lastACKrcvd):
                for i in range((self.lastACKrcvd + 1), (packet.seq_num + 1)):
                    if (self.buffer[i % SWPSender._SEND_WINDOW_SIZE] is not None):
                        # Cancel timer and remove packet from array
                        self.buffer[i % SWPSender._SEND_WINDOW_SIZE][1].cancel()
                        self.buffer[i % SWPSender._SEND_WINDOW_SIZE] = None
                self.lastACKrcvd = packet.seq_num
                self.semaphore.release()
        
        return

class SWPReceiver:
    _RECV_WINDOW_SIZE = 5

    def __init__(self, local_address, loss_probability=0):
        self._llp_endpoint = llp.LLPEndpoint(local_address=local_address, 
                loss_probability=loss_probability)

        # Received data waiting for application to consume
        self._ready_data = queue.Queue()

        # Start receive thread
        self._recv_thread = threading.Thread(target=self._recv)
        self._recv_thread.start()
        
        # Additional state variables
        self.LastFrameRead = 0
        self.buffer = [None] * SWPReceiver._RECV_WINDOW_SIZE
        self.mutex = threading.BoundedSemaphore(value = 1)
        
    def recv(self):
        return self._ready_data.get()

    def _recv(self):
        while True:
            # Receive data packet
            raw = self._llp_endpoint.recv()
            packet = SWPPacket.from_bytes(raw)
            logging.debug("Received: %s" % packet)

            self.mutex.acquire()

            if (packet.seq_num > self.LastFrameRead):
                temp_list = [packet.seq_num, packet.data]
                self.buffer[packet.seq_num % SWPReceiver._RECV_WINDOW_SIZE] = temp_list
                
                for i in range((self.LastFrameRead + 1), (packet.seq_num + 1)):
                    if (self.buffer[i % SWPReceiver._RECV_WINDOW_SIZE] is not None):
                        self._ready_data.put(self.buffer[i % SWPReceiver._RECV_WINDOW_SIZE][1])
                        self.LastFrameRead = self.buffer[i % SWPReceiver._RECV_WINDOW_SIZE][0]
                        self.buffer[i % SWPReceiver._RECV_WINDOW_SIZE] = None
                    else:
                        break
                                
            # Send ACK and print data
            ackPacket = SWPPacket(SWPType.ACK, self.LastFrameRead, b'')
            self.mutex.release()

            self._llp_endpoint.send(ackPacket.to_bytes())
            logging.debug("Sent: %s" % ackPacket)
   
        return
