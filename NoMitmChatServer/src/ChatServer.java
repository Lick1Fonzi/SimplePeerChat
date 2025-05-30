import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/*
 * Server main class. Starts listening socket, and starts a new connection handler for each incoming connection from a user.
 */
public class ChatServer {
	
	static final private int serverPort = 6969;
	/*
	 * Server main
	 */
	public static void main(String[] args) {
		
		ArrayList<User> usersConnected;
		usersConnected = new ArrayList<>();
		
		ServerSocket ssoc = null;
		try {
			ssoc = new ServerSocket(serverPort);
			ssoc.setReuseAddress(true);
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			
			while(true) {
				Socket client = ssoc.accept();
				connectionHandler cHand = new connectionHandler(client, usersConnected);
				new Thread(cHand).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
	

