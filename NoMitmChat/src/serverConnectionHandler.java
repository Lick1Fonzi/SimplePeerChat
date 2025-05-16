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

public class serverConnectionHandler implements Runnable{

	private InetAddress ip;
	private int port;
	private PeerChat main;
	private String username;
	private PrintWriter out;
	private BufferedReader in;
	
	public serverConnectionHandler(InetAddress ip, int port,String username, PeerChat main){
		this.ip = ip;
		this.port = port;
		this.main = main;
		this.username = username;
		main.loggedIn();
		out = null;
		in = null;
	}
	
	public boolean isPortAvail(int port) {
		try {
			DatagramSocket ds = new DatagramSocket(port);
			ds.close();
			return true;
		}catch(Exception e) {
			return false;
		}
	}

	public void getUserList() {
		this.getOut().println("LISTUSERS");
	}
	
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
	
	public void startNewChat(String otheruser,InetAddress othip,int port) {
		try {
			Contact me = new Contact(main.getUserNameField().getText(),InetAddress.getLocalHost(),port);
			Contact other = new Contact(otheruser,othip,port);
			new ChatUI(me,other);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
	}
	
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

	public PrintWriter getOut() {
		return out;
	}

	public BufferedReader getIn() {
		return in;
	}
	

}
