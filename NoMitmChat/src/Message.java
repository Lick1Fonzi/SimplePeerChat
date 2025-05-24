import java.util.stream.Stream;
/*
 * Rapresents a Peer Chat protocol message. It is formed by the username, type of operation, and parameters
 */
public class Message {
	
	public final static String CONNECT = "Connect";
	public final static String MSG = "Msg";
	public final static String CLOSE = "Close";
	public final static String KEEPALIVETOKEN = "KeepAliveToken";
	
	private String type;
	private String txt;
	private String user;
	public Message(String user,String type,String txt) {
		this.type = type;
		this.txt = txt;
		this.user = user;
	}
	public Message(String protocolMsg) {
		Stream<String> s = protocolMsg.lines();
		String str[] =  s.toArray(String[]::new);
		this.user = str[0];
		this.type = str[1];
		this.txt = str[2];
	}
	/*
	 * Unifies in a single line all fields of the message. Ready to be sent out.
	 */
	public String unify() {
		return user + "\n" + type + "\n" + txt + "\n\n";
	}
	/*
	 * Transforms the messages for the UI view, as the receiving peer will see it.
	 */
	public String chatLine() {
		return user + ": " + txt + "\n";
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
}
