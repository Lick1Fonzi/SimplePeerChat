/*
 * Class of messages exchanged between client and server. Here are defined the protocol possible operations.
 */
public class ServerMsg {

	public static final String OP_REGISTER = "REGISTER";
	public static final String OP_CHAT = "CHAT";
	public static final String OP_LISTUSERS = "LISTUSERS";
	public static final String OP_HELLO = "HELLO";
	public static final String OP_CLOSE = "CLOSE";
	public static final String ERROR = "ERROR";
	
	private String operation;
	private String[] content;
	
	public ServerMsg(String op, String[] content) {
		this.content = content;
		this.operation = op;
	}
	
	public ServerMsg(String txt) {
		String[] split = txt.split(" ");
		this.operation = split[0];
		this.content = new String[split.length-1];
		System.arraycopy(split, 1, content, 0, split.length - 1);
		/*for(String s: content) {
			System.out.println(s);
		}
		System.out.println(this.operation);*/
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getUsername() {
		if(this.operation.equals(OP_REGISTER) || this.operation.equals(OP_CHAT)) {
			return this.content[0];
		}
		else {
			return "";
		}
	}

}
