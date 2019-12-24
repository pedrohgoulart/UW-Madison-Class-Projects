import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

public class Server {
	private ServerSocket serverSoc;
	private Socket clientSoc;
	private double totalTime;
	private long totalReadBytes;
	private int byteSize = 1000;
	private byte [] bytes;
	
	public Server (int serverPort) throws IOException {
		this.serverSoc = new ServerSocket(serverPort);
		this.clientSoc = this.serverSoc.accept();
		this.totalReadBytes = 0;
		this.bytes = new byte[byteSize];
	}
	
	public void receiveData() throws IOException {
		// Save start time of first byte sent
		totalTime = new Timestamp(System.currentTimeMillis()).getTime();
		
		int readBytes = 0;
		while(readBytes !=-1) {
			totalReadBytes += readBytes;
			readBytes = clientSoc.getInputStream().read(bytes, 0, byteSize);
		}

		// Get difference between times
		totalTime = Math.abs(totalTime - new Timestamp(System.currentTimeMillis()).getTime());
		
		// Close sockets
		serverSoc.close();
		clientSoc.close();
		
		// Print summary
		System.out.println("received=" + getReceived() + " KB rate=" + getRate() + " Mbps");
	}
	
	private long getReceived() {
		return (totalReadBytes / 1000);
	}
	
	private String getRate() {
		return String.format("%.3f", ((totalReadBytes * 8) / 1000) / totalTime);
	}
}


	