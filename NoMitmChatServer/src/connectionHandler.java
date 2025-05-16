import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class connectionHandler implements Runnable{

	private ArrayList<User> listusers;
	private Socket client;
	private User user;
	private PrintWriter out; 
    private BufferedReader in;
	private boolean registered;
	
	public connectionHandler(Socket client, ArrayList<User> usersConnected) {
		
		this.client = client;
		this.user = null;
		this.out = null; 
        this.in = null; 
        this.registered = false;
        this.listusers = usersConnected;
	}

	public boolean isPortAvail(int nport, BufferedReader in, PrintWriter out) {
		String line = null;
		try {
			out.println("PORT " + String.valueOf(nport));
			line = in.readLine();
			System.out.println("AFTER PORT: " + line);
			//line = line.split(" ")[0];
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return line.equals("OK");
//		if(line.equals("OK"))
//			return true;
//		else
//			return false;
	}
	
	public PrintWriter getOut() {
		return out;
	}

	public BufferedReader getIn() {
		return in;
	}
	
	public void startChat(InetAddress ip,int nport,String user) {
		this.out.println("STARTCHAT " + ip.getHostAddress() + " " + String.valueOf(nport) + " " + user);
	}

	@Override
	public void run() {
		  		 
        try {
        	// outputstream of client
			out = new PrintWriter(client.getOutputStream(), true);
            // inputstream of client 
            in = new BufferedReader(new InputStreamReader(client.getInputStream())); 
				
                String line; 
				while ((line = in.readLine()) != null) { 
					System.out.printf(" Sent from client %s - [%s]: %s\n", client.getInetAddress().getHostAddress(), (this.getUser() == null ? "Guest" : this.getUser().getUsername()), line); 
					//out.println(line);
					
					ServerMsg smsg = new ServerMsg(line);
					String operation = smsg.getOperation();
					switch(operation) {
					
					case ServerMsg.OP_HELLO:
						out.println(ServerMsg.OP_HELLO);
						break;
						
					case ServerMsg.OP_REGISTER:
						boolean exists = false;
						for(User u: listusers) {
							if(u.getUsername().equals(smsg.getUsername()))
								exists = true;
						}
						if(exists) {
							out.println(ServerMsg.ERROR + " User_exists");
						}
						else if(registered) {
							out.println(ServerMsg.ERROR + " Already_auth");
						}
						else {
						this.user = new User(smsg.getUsername(),client.getInetAddress(), this);
						listusers.add(this.user);
						registered = true;
						out.println("REGISTERED " + smsg.getUsername() + " OK");
						}
						break;
						
					case ServerMsg.OP_LISTUSERS:
						if(registered) {
							out.println("STARTUSERS");
							for(User u: listusers) {
								out.println(u.getUsername());
							}
							out.println("ENDUSERS");
						}
						else {
							out.println(ServerMsg.ERROR + " Not_registered");
						}
						break;
						
					case ServerMsg.OP_CHAT:
						if(registered) {
							boolean ex = false;
							String tmp = smsg.getUsername();
							User other = null;
							for(User u: listusers) {
								if(u.getUsername().equals(smsg.getUsername())) {
									ex = true;
									other = u;
								}
							}
							if(!ex) {
								out.println(ServerMsg.ERROR + " User_not_online");
								break;
							}
							else {
								out.println("OK");
							}
							
							boolean one = false;
							boolean two = false;
							int ntry = 0;
							connectionHandler othch = other.getCh();
							int port = 60000;
							while(!(one && two) && (ntry < 20)) {
							port = (new Random().nextInt(60000 - 40000)) + 40000 ;
							
							one = this.isPortAvail(port, in, out);
							two = othch.isPortAvail(port, othch.getIn(), othch.getOut());
//							System.out.println(one);
//							System.out.println(two);
							
//							channelChecker mecheck = new channelChecker(this,port);
//							channelChecker ucheck = new channelChecker(othch,port);
//							
//							Thread met = new Thread(mecheck);
//							met.start();
//							Thread ut = new Thread(ucheck);
//							ut.start();
//							
//							met.join();
//							ut.join();

//							one = mecheck.getAvail();
							System.out.println("Ended connection with me, avail:" + String.valueOf(one));
							
//							two = ucheck.getAvail();
							System.out.println("Ended connection with other, avail:" + String.valueOf(two));
							
							ntry++;
							}
							if(ntry > 20) {
								out.println("ERROR Max_PortAgree_Retries_Exceded");
								break;
							}

							Thread met = new Thread(new chatStarter(this,othch,port));
							met.start();
							
							Thread ut = new Thread(new chatStarter(othch,this,port));
							ut.start(); 
							
							met.join();
							ut.join(); 
							
							//startChat(othch.getUser().getIp(),port,othch.getUser().getUsername());
							//othch.startChat(this.getUser().getIp(),port,this.getUser().getUsername());
							
						}else {
							out.println(ServerMsg.ERROR + " Not_registered");
						}
						break;
						
					case ServerMsg.OP_CLOSE:
						out.println(ServerMsg.OP_CLOSE);
						throw new IOException("Connection Closed by " + client.getInetAddress().getHostAddress());
						
					default:
						out.println("ERROR Operation_unknown");
					}
				}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
        finally {
        	try {
        		out.close();
        		in.close();
        		client.close();
        		if(registered) {
        			listusers.remove(this.user);
        		}
        	}catch(IOException e) {
        		e.printStackTrace();
        	}
        	System.out.println("Done at end of user connection");
        }
	}

	private class channelChecker implements Runnable{

		private connectionHandler ch;
		private int port;
		private boolean avail;
		public channelChecker(connectionHandler ch,int port) {
			this.ch=ch;
			this.port = port;
			this.avail = false;
		}
		
		@Override
		public void run() {
			avail = this.ch.isPortAvail(port, this.ch.getIn(), this.ch.getOut());
		}
		public boolean getAvail() {
			return this.avail;
		}
		
	}
	
	private class chatStarter implements Runnable{
		private connectionHandler mech,youch;
		private int port;
		public chatStarter(connectionHandler mech,connectionHandler youch,int port) {
			this.mech = mech;
			this.youch = youch;
			this.port = port;
		}
		@Override
		public void run() {
			System.out.println("starting chat to" + youch.getUser().getUsername() );
			mech.startChat(youch.getUser().getIp(), port, youch.getUser().getUsername());
		}
	}
	
	public User getUser() {
		return user;
	}	

}
