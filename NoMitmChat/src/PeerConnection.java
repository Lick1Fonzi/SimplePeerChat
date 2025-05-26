import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;

/*
 * Peer connection Thread class. 
 */
public class PeerConnection implements Runnable{

	private DatagramSocket sock;
	private boolean connected;
	private boolean closed;
	
	int lPort;
	int rPort;
	InetAddress otherIp;
	int keepAliveToken;
	int num;
	Contact me,other;
	Receiver r;
	JTextArea txtarea;
	
	public PeerConnection(Contact me, Contact other,JTextArea txtarea) throws SocketException {
		connected = false;
		this.otherIp = other.getIp();
		this.lPort = me.getPort();
		sock = new DatagramSocket(lPort);
		this.rPort = other.getPort();
		this.keepAliveToken = 0;
		this.num = (new Random()).nextInt();
		this.me = me;
		this.other = other;
		this.closed = false;
		this.txtarea = txtarea;
		txtarea.setText("Not connected");
		this.r = new Receiver(this);

	}
	
	/*
	 * Starts a new listening thread. Manages peer chats start of the chat, sending repeatedly CONNECT messages. 
	 */
	@Override
	public void run() {
		Thread t = new Thread(r);
		t.start();
		while(!closed) {
			if(!connected) {
				try {
					Message m = new Message(me.getUsername(),Message.CONNECT,String.valueOf(num));
					sendMsg(m.unify());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	/*
	 * Sends in output UDP socket the message that the user types
	 */
	public void sendMsg(String msg) throws IOException {
		byte buf[] = msg.getBytes();
		DatagramPacket dp = new DatagramPacket(buf,buf.length,this.otherIp,rPort);
		sock.send(dp);
	}

    public static StringBuilder data(byte[] a) 
    { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        int i = 0; 
        while (a[i] != 0) 
        { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret; 
    } 
    /*
     * Manages incoming messages, sends out messages and updates UI. Sends repeatedly a keep alive token to other.
     * After token is received, a timer is started to see if the connection timed out and other is disconnected 
     */
    public class Receiver implements Runnable{
    	PeerConnection pc;
    	Message token;
    	boolean hasToken;
    	Timer timer;
    	TokenTimer toktime;
    	public Receiver(PeerConnection pc) {
    		this.pc = pc;
    		this.token =  new Message(this.pc.me.getUsername(),Message.KEEPALIVETOKEN,"");
    		this.timer = new Timer();
    		toktime = new TokenTimer();
    	}
    	/*
    	 * Sends close connection message to stop chatting
    	 */
    	public void closeConnection() {
    		Message close = new Message(me.getUsername(),Message.CLOSE,"");
    		try {
				sendMsg(close.unify());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	/*
    	 * Body of thread, managing connect, keepalive and actual chat messages
    	 */
		@Override
		public void run() {
			while(!closed) {
				byte[] receive = new byte[65535];
				DatagramPacket DpReceive = new DatagramPacket(receive, receive.length); 
				  
		        try {
					pc.sock.receive(DpReceive);
					Message m = new Message(data(receive).toString());
					//System.out.println(m.unify());
					
					String type = m.getType();
					if(type.equals(Message.CONNECT)) {
						if(!connected) {
							connected = true;
							txtarea.setText("Connected\n");
							System.out.println("Connect from: " + m.unify());
							int x = Integer.parseInt(m.getTxt());
							// Bully to decide who starts sending token
							if(x < this.pc.num) {
								this.pc.sendMsg(token.unify());
							}
						}
						else {
							Message m1 = new Message(me.getUsername(),Message.CONNECT,String.valueOf(num));
							sendMsg(m1.unify());
						}
					}
					else if(connected && type.equals(Message.KEEPALIVETOKEN)) {
						System.out.println("Token alive");
						try {
							toktime.cancel();
						}catch(Exception e) {
							;
						}
						this.pc.sendMsg(token.unify());
						toktime = new TokenTimer();
						timer.schedule(toktime, 20000);
					}
					else if(connected && type.equals(Message.MSG)) {
						txtarea.append(m.chatLine());
						System.out.println("Msg from: " + m.unify());
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			txtarea.setText("Not connected\n");
		}
    }
    /*
     * Class to check the time out of a connection
     */
    public class TokenTimer extends TimerTask{
		@Override
		public void run() {
			System.out.println("Timed Out");
			closed = true;
		}
    }
	
}
