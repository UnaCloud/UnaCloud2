package communication;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Used as standard for send message in UDP Protocol
 * @author Cesar
 *
 */
public class UnaCloudMessageUDP implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8785226165731609571L;
	private String message;
	private String host;
	private String ip;
	private int port;
	private UDPMessageEnum type;
	
	public UnaCloudMessageUDP(){
		
	}
	
	public UnaCloudMessageUDP(String message, String ip, int port, String host, UDPMessageEnum type) {
		super();
		this.message = message;
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
	
	public UDPMessageEnum getType() {
		return type;
	}
	
	public void setType(UDPMessageEnum type) {
		this.type = type;
	}
	
	public byte[] generateByteMessage(){
		return (type.name()+"_"+message).getBytes();
	}
	
	public void setMessageByBytes(byte[] bytes) throws UnsupportedEncodingException{
		String[] mesg = new String(bytes, "UTF-8").split("_");
		this.message = mesg[1];
		this.type = UDPMessageEnum.getType(mesg[0]);
	}

}
