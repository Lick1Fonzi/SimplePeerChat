import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

public class ChatUI extends JFrame{
	
    private JTextArea chatArea;
    private JTextField inputField;
    PeerConnection pc;
    Contact me,other;
	
	public ChatUI(Contact me, Contact other) throws UnknownHostException, SocketException {
		this.me = me; this.other = other;
		
		setTitle("Chat Interface");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        /*
        // Left Column (Scrollable List)
        listModel = new DefaultListModel<>();
        
        textList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(textList);
        listScrollPane.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(listScrollPane, BorderLayout.WEST);
		*/
        
        // Center Panel (Chat Area)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Bottom Panel (Input Field and Button)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        bottomPanel.add(inputField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        bottomPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        setVisible(true);
        
        pc = new PeerConnection(me, other, chatArea);
        Thread tpc = new Thread(pc);
        tpc.start();
        
        sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clickEnterMsg();
			}
        	
        });
        inputField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				clickEnterMsg();
				}
			}
			@Override
			public void keyTyped(KeyEvent e) {
				// pass
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// pass
			}
        });
        
	}
	
	void clickEnterMsg() {
		String m = inputField.getText();
		Message msg = new Message(me.getUsername(),Message.MSG,m);
		try {
			pc.sendMsg(msg.unify());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		chatArea.append(msg.chatLine());
	inputField.setText("");
	}
}
