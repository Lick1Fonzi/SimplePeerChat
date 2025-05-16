import java.net.InetAddress;

public class Contact {
	private String username;
	private String key;
	private InetAddress ip;
	private int port;
	public Contact(String username, InetAddress ip, int port) {
		this.username = username;
		this.ip = ip;
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public InetAddress getIp() {
		return ip;
	}
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	

}
