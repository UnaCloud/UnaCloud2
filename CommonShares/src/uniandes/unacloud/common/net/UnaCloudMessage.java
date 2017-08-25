package uniandes.unacloud.common.net;

import java.io.Serializable;
/**
 * Used as standard message to be used in TCP and UDP Protocols
 * @author CesarF
 *
 */
public class UnaCloudMessage implements Serializable {
	
	private static final long serialVersionUID = -8785226165731609571L;	
	
	private String host;
	
	private String ip;
	
	private int port;
	
	private String type;
	
	
	/**
	 * Creates an empty UDP message
	 */
	public UnaCloudMessage() {
		
	}
	
	/**
	 * Creates a new message using values in another message
	 */
	public UnaCloudMessage(UnaCloudMessage message) {	
		this.ip = message.getIp();
		this.port = message.getPort();
		this.host = message.getHost();
		this.type = message.getType();
	}
	
	/**
	 * Creates a new message with data from IP, port, host and message type
	 */
	public UnaCloudMessage(String ip, int port, String host, String type) {
		this.ip = ip;
		this.port = port;
		this.host = host;
		this.type = type;
	}

	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "UnaCloudMessage [host=" + host + ", ip=" + ip + ", port="
				+ port + ", type=" + type + "]";
	}	

}
