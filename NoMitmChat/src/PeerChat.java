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

/*
 * Client class.
 * Sets up the desktop application and launches connections with a server that keeps a register of connected users. 
 * It needs the ip address of the server, the port that exposes (default 6969) and the username of the user registering in the corresponding text fields.
 * If registered, the corresponding button will ask the server the list of connected users. I a user is clicked on, it will try to start a chat with him opening a UI chat window.
 * Can launch directly peer connections to others if info are known (the other's ip and a port otherwise agreed) to start a chat, without the need to be registered to a server.
 */
public class PeerChat{
	
	private boolean isLoggedIn;
	private JLabel status;
	private JTextField userNameField;
	private serverConnectionHandler conserv;

	/*
	 * PeerChat constructor
	 */
    public PeerChat() {
        this.isLoggedIn = false;
        status = null;
        userNameField = null;
        conserv = null;
    }

    /*
     * Getter of Username TextField object
     */
    public JTextField getUserNameField() {
		return userNameField;
	}

    /*
     * Sets logged in flag
     */
	public void loggedIn() {
    	this.isLoggedIn = true;
    }
    /*
     * Sets logged out flag
     */
    public void signedOut() {
    	this.isLoggedIn = false;
    }
    /*
     * Change the label showing the status of the connection with the server 
     */
    public void changeStatus(String s) {
    	status.setText(s);
    }
    /*
     * Starts the handler communicating with the server
     */
    public serverConnectionHandler connServer(InetAddress ip, int port,String username) {
    	return new serverConnectionHandler(ip,port,username,this);
    }
    /*
     * Sets up the main panel with all Swing components and the action listeners for the buttons to start connections with both peers and server
     */
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
	
    /*
     * Main, creates new Desktop app of the client 
     */
	public static void main(String[] args) throws SocketException {
		
		 	JFrame frame = new JFrame();

	        PeerChat panel = new PeerChat();
	        frame.add(panel.getPanel());

	        frame.setSize(650, 200);
	        frame.setVisible(true);
		
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
