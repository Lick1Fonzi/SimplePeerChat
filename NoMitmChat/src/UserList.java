import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class UserList extends JFrame{

	private JPanel pan;
	private JScrollPane scroll;
	public UserList(ArrayList<String> users, serverConnectionHandler sch) {
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 300);
		
		pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		scroll = new JScrollPane();
		
		for(String u: users) {
			JButton butt = new JButton(u);
			butt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sch.askChat(u);
//					Thread tmp = new Thread(new chatAsker(sch,u));
//					tmp.start();
//					try {
//						tmp.join();
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
					dispose();
				}
			});
			pan.add(butt);
		}
		//scroll.add(pan);
		
		this.add(pan);
		this.setVisible(true);
		
	}

	private class chatAsker implements Runnable{
		serverConnectionHandler sch;
		String user;
		public chatAsker(serverConnectionHandler sch,String user) {
			this.sch = sch;
			this.user = user;
		}
		@Override
		public void run() {
			sch.askChat(user);
		}
		
		
	}
	
}
