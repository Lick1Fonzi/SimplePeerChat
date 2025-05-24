import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/*
 * Handler Thread class of the connection of the client with the server
 */
public class serverConnectionHandler implements Runnable{

	private InetAddress ip;
	private int port;
	private PeerChat main;
	private String username;
	private PrintWriter out;
	private BufferedReader in;
	
	/*
	 * Constructor setting up field passed from JFrame like server's ip and port and username of user
	 */
	public serverConnectionHandler(InetAddress ip, int port,String username, PeerChat main){
		this.ip = ip;
		this.port = port;
		this.main = main;
		this.username = username;
		main.loggedIn();
		out = null;
		in = null;
	}
	/*
	 * Checks if specified port is available to open a socket
	 */
	public boolean isPortAvail(int port) {
		try {
			DatagramSocket ds = new DatagramSocket(port);
			ds.close();
			return true;
		}catch(Exception e) {
			return false;
		}
	}

	/*
	 * Ask the server the list of user connected through LISTUSERS protocol message
	 */
	public void getUserList() {
		this.getOut().println("LISTUSERS");
	}
	/*
	 * Ask the server to initiate protocol to start chatting with specified user through CHAT message
	 */
	public void askChat(String user) {
		try {
			this.getOut().println("CHAT "+ user);
//			String err;
//			err = this.getIn().readLine();
//			System.out.println("in askchat: " + err);
//			if(err.contains("ERROR")) {
//				this.main.changeStatus(err);
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * Start a peer connection with specified user to chat
	 */
	public void startNewChat(String otheruser,InetAddress othip,int port) {
		try {
			Contact me = new Contact(main.getUserNameField().getText(),InetAddress.getLocalHost(),port);
			Contact other = new Contact(otheruser,othip,port);
			new ChatUI(me,other);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
	}
	/*
	 * Body of thread function that manages socket. Before listening and responding to server messages, it then registers to the server as a connected user.
	 * If it receives a STARTUSERS...ENDUSERS msg, the user asked through the app the list of connected user.
	 * If it receives a PORT msg, a handshake to decide the port to use is in place.
	 * If it receives a STARTCHAT msg,creates a new peer connection with user specified in server message.
	 */
	@Override
	public void run() {
		
		Socket sock = null;
		
		try {
			sock = new Socket(ip,port);
			out = new PrintWriter(sock.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String line = null;
			out.println("HELLO");
			line = in.readLine();
			if(!line.equals("HELLO")) {
				throw new IOException();
			}
			out.println("REGISTER " + this.username);
			line = in.readLine();
			if(line.equals("ERROR User_exists")) {
				main.changeStatus("User Exists");
				throw new IOException();
			}else if(!line.equals("REGISTERED " + this.username + " OK")){
				System.out.println(line);
				main.changeStatus("ERROR Registering");
				throw new IOException();
			}
			main.changeStatus("Register ok");
			
			while((line = in.readLine()) != null) {
				System.out.println("In conn handler: " + line);
				if(line.contains("PORT")) {
					
					int port = Integer.parseInt(line.split(" ")[1]);
					if(isPortAvail(port)) {
						out.println("OK");
						out.println("OK");
					}
					else {
						out.println("ERROR");
					}
				}
				else if(line.contains("STARTCHAT")) {
						String[] tmp = line.split(" ");
						InetAddress addr = InetAddress.getByName(tmp[1]);
						int p = Integer.parseInt(tmp[2]);
						String othuser = tmp[3];
						startNewChat(othuser,addr,p);
						System.out.println("starting new chat from " + username + "with: " + othuser);
						//Contact me = new Contact(main.getUserNameField().getText(),InetAddress.getLocalHost(),p);
						//Contact other = new Contact(othuser,addr,p);
						//new ChatUI(me,other);
				}
				else if (line.contains("STARTUSERS")) {
					ArrayList<String> Users = new ArrayList<String>();
					try {
						line = this.getIn().readLine();
						System.out.println(line);
						while(!line.equals("ENDUSERS")) {
							if(!line.equals(main.getUserNameField().getText())) {
								Users.add(line);
							}
							
							line = this.getIn().readLine();
						}
						new UserList(Users,this);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(line.equals("ERROR Operation_unknown")) {
					continue;
				}
			}
			
		}catch(IOException e) {
			try {
				in.close();
				out.close();
				sock.close();
				main.signedOut();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			;
		}
	}

	/*
	 * Socket input getter
	 */
	public PrintWriter getOut() {
		return out;
	}
	/*
	 * Socket input getter
	 */
	public BufferedReader getIn() {
		return in;
	}
	

}
