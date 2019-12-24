import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;

public class Client {
	private Socket clientSoc;
	private OutputStream out;
	private double totalTime;
	private long totalWriteBytes;
	private int byteSize = 1000;
	
	public Client(String host, int portNumber) throws IOException {
		this.clientSoc = new Socket(host, portNumber);
		this.out = clientSoc.getOutputStream();
		this.totalWriteBytes = 0;
	}
	
	public void sendData(int time) throws IOException {
		Timestamp initialTime = new Timestamp(System.currentTimeMillis());
		Timestamp currentTime = initialTime;
		totalTime = 0;
		
		while(totalTime < time) {
			byte[] bytes = new byte[byteSize];
			out.write(bytes);
			totalWriteBytes += bytes.length;
			currentTime = new Timestamp(System.currentTimeMillis());
			totalTime = Math.abs(initialTime.getTime() - currentTime.getTime());
		}
		
		// Close sockets
		clientSoc.close();
		
		// Print summary
		System.out.println("sent=" + getSent() + " KB rate=" + getRate() + " Mbps");
	}
	
	private long getSent() {
		return (totalWriteBytes / 1000);
	}
	
	private String getRate() {
		return String.format("%.3f", ((totalWriteBytes * 8) / 1000) / totalTime);
	}
}