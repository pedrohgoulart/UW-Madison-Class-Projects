import java.io.IOException;

public class Iperfer {
	public static void main(String[] args) {
		if (args.length == 0) {
			invalidArguments();
		}
		
		if (args[0].equals("-c")) {
			// Check arguments provided
			if (args.length != 7 || !(args[1].equals("-h") && args[3].equals("-p") && args[5].equals("-t"))) {
				invalidArguments();
			}
			
			// Check variables provided
			String host = checkHost(args[2]);
			int port = checkInteger(args[4]);
			int time = checkInteger(args[6]) * 1000; // Time in milliseconds
			
			checkPortRange(port);
			
			if (time < 0) {
				invalidArguments();
			}
					
			// Call connection to client
			try {
				Client c = new Client(host, port);
				c.sendData(time);
			} catch (IOException e) {
				System.out.println(e);
				System.exit(1);
			}
		} else if (args[0].equals("-s")) {
			// Check arguments provided
			if (args.length != 3 || !args[1].equals("-p")) {
				invalidArguments();
			}
			
			// Check value provided
			int port = checkInteger(args[2]);
			
			checkPortRange(port);
			
			// Call connection to server
			try {
				Server s = new Server(port);
				s.receiveData();
			} catch (IOException e) {
				System.out.println(e);
				System.exit(1);
			}
		} else {
			invalidArguments();
		}
	}
	
	private static void invalidArguments() {
		System.out.println("Error: invalid arguments");
		System.exit(1);
	}
	
	private static String checkHost(String str) {
		String checkNumber = "";
		int dotCounter = 0;  
		
		for (int i = 0; (i < str.length()); i++) {  
			if (str.charAt(i) == '.') {
				dotCounter++;
				int tempInt = checkInteger(checkNumber);
				if (tempInt < 0 || tempInt > 255) {
					invalidArguments();
				}
				checkNumber = "";
			} else {
				checkNumber += str.charAt(i);
			}
		}
		
		if (dotCounter != 3) {
			invalidArguments();
		}
		
		return str;
	}
	
	private static int checkInteger(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			invalidArguments();
		}
		return -1;
	}
	
	private static void checkPortRange(int range) {
		if (range < 1024 || range > 65535) {
			System.out.println("Error: port number must be in the range 1024 to 65535");
			System.exit(1);
		}
	}
}
