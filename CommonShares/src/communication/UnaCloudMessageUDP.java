package communication;

/**
 * Used as standard for send message in UDP Protocol
 * @author Cesar
 *
 */
public class UnaCloudMessageUDP {
	
	private String message;
	private String host;
	private String ip;
	private int port;
	
	public UnaCloudMessageUDP(String message, String ip, int port, String host) {
		super();
		this.message = message;
		this.ip = ip;
		this.port = port;
		this.host = host;
	}

	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
