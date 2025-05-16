import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PeerChat{
	
	private boolean isLoggedIn;
	JLabel status;
	JTextField userNameField;
	serverConnectionHandler conserv;

    public PeerChat() {
        this.isLoggedIn = false;
        status = null;
        userNameField = null;
        conserv = null;
    }

    public JTextField getUserNameField() {
		return userNameField;
	}

	public void loggedIn() {
    	this.isLoggedIn = true;
    }
    public void signedOut() {
    	this.isLoggedIn = false;
    }
    
    public void changeStatus(String s) {
    	status.setText(s);
    }
    
    public serverConnectionHandler connServer(InetAddress ip, int port,String username) {
    	return new serverConnectionHandler(ip,port,username,this);
    }
    
    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton serverConfigButton = new JButton("Server Config");
        panel.add(serverConfigButton);
        
        JTextField serverAddressField = new JTextField(16); 
        serverAddressField.setText("192.168.1.138");
        panel.add(serverAddressField);

        JTextField serverPortField = new JTextField(5);
        serverPortField.setText("6969");
        panel.add(serverPortField);
        
        userNameField = new JTextField(20);
        userNameField.setText("Username");
        panel.add(userNameField);
        
        status = new JLabel();
        status.setText("Not Connected");
        panel.add(status);
        
        JButton userList = new JButton("Get Users Online");
        panel.add(userList);

        
        JLabel empty = new JLabel();
        empty.setText("                                    ");
        panel.add(empty);
        
        JButton newChatButton = new JButton("New Chat");
        panel.add(newChatButton);
        
        JTextField peerAddressField = new JTextField(16); 
        peerAddressField.setText("Peer Address");
        panel.add(peerAddressField);

        JTextField peerPortField = new JTextField(5);
        peerPortField.setText("Port");
        panel.add(peerPortField);
        
        newChatButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					Contact tmp1 = new Contact("anon",InetAddress.getByName(peerAddressField.getText()),Integer.parseInt(peerPortField.getText()));
					Contact me	= new Contact(userNameField.getText(),InetAddress.getLocalHost(),Integer.parseInt(peerPortField.getText()));
					new ChatUI(me,tmp1);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
        	
        });
        
        serverConfigButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isLoggedIn) {
					try {
						conserv = connServer(InetAddress.getByName(serverAddressField.getText()),Integer.valueOf(serverPortField.getText()),userNameField.getText());
						new Thread(conserv).start();;
					} catch (NumberFormatException | UnknownHostException e1) {
						changeStatus("Invalid Config");
						e1.printStackTrace();
					}
				}	
			}
        });
        
        userList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(isLoggedIn) {
					conserv.getUserList();
				}
			}
        });

        return panel;
    }
	
	public static void main(String[] args) throws SocketException {
		
		 	JFrame frame = new JFrame();

	        PeerChat panel = new PeerChat();
	        frame.add(panel.getPanel());

	        frame.setSize(650, 200);
	        frame.setVisible(true);
		
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        
	    /*
        try {
        	Contact other;
        	Contact me;
			Contact tmp1 = new Contact("user1",InetAddress.getByName("127.0.0.1"),5555);
	        Contact tmp2 = new Contact("user2",InetAddress.getByName("127.0.0.1"),6666);
	        other = tmp2;
	        me = tmp1;
	        if(args.length > 0) {
	        	other = tmp1;
	        	me = tmp2;
	        }
	        
			new ChatUI(me,other);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		


	}
}
