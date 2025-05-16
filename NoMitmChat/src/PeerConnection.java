import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import javax.swing.JTextArea;

public class PeerConnection implements Runnable{

	DatagramSocket sock;
	boolean connected;
	boolean closed;
	
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
	
	
	public void closeConnection() {
		
	}

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
    
    public class Receiver implements Runnable{
    	PeerConnection pc;
    	public Receiver(PeerConnection pc) {
    		this.pc = pc;
    	}
		@Override
		public void run() {
			while(!closed) {
				byte[] receive = new byte[65535];
				DatagramPacket DpReceive = new DatagramPacket(receive, receive.length); 
				  
		        // Step 3 : receive the data in byte buffer. 
		        try {
					pc.sock.receive(DpReceive);
					Message m = new Message(data(receive).toString());
					//System.out.println(m.unify());
					
					String type = m.getType();
					if(type.equals(Message.CONNECT) && !connected) {
						connected = true;
						txtarea.setText("Connected\n");
						System.out.println("Connect from: " + m.unify());
					}
					else if(type.equals(Message.MSG)) {
						txtarea.append(m.chatLine());
						System.out.println("Msg from: " + m.unify());
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
    }
	
}
