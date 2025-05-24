import java.net.InetAddress;
/*
 * User entity for the server, with a reference to its connection handler.
 */
public class User {
	
	private String username;
	private InetAddress ip;
	private connectionHandler ch;
	
	public User(String username,InetAddress ip, connectionHandler ch) {
		this.username = username;
		this.ip=ip;
		this.ch = ch;
	}

	public String getUsername() {
		return username;
	}

	public InetAddress getIp() {
		return ip;
	}

	public connectionHandler getCh() {
		return ch;
	}

	
	
}
