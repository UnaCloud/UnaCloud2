package uniandes.unacloud.common.net;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

/**
 * Used as standard message to be used in TCP and UDP Protocols
 * @author CesarF
 *
 */
public class UnaCloudMessage implements Serializable {
	
	private static final long serialVersionUID = -8785226165731609571L;
	
	private static final String TAG_HOST = "host";
	
	private static final String TAG_IP = "ip";
	
	private static final String TAG_PORT = "port";
	
	private static final String TAG_TYPE_MESSAGE = "type_message";
	
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
	
	/**
	 * generates a byte array to send message based in components in message
	 * @return byte array
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] generateByteMessage() throws UnsupportedEncodingException {
		String messageString = this.getStringMessage();
		return messageString.getBytes("UTF-8");
	}
	
	/**
	 * Return the String of JSON Message
	 * @return String
	 */
	public String getStringMessage() {		
		StringWriter out = new StringWriter();
		getJsonMessage().write(out);		
		String jsonText = out.toString();
		return jsonText;		
	}
	
	/**
	 * Return a json object with all attributes
	 * @return
	 */
	protected JSONObject getJsonMessage(){
		JSONObject total = new JSONObject();		
		total.put(TAG_HOST, this.host);
		total.put(TAG_IP, this.ip);
		total.put(TAG_PORT, this.port);
		total.put(TAG_TYPE_MESSAGE, this.type);
		return total;
	}
	
	/**
	 * Transforms a byte array in parts of message (JSON Format)
	 * @param bytes
	 * @throws UnsupportedEncodingException
	 */
	public void setMessageByBytes(byte[] bytes) throws UnsupportedEncodingException {
		String tempMessage = new String(bytes, "UTF-8");
		this.setMessageByStringJson(tempMessage);
	}

	/**
	 * Reads the String of a message and set with the variables.
	 * @param format
	 */
	public void setMessageByStringJson(String format) {
		JSONObject json;
		json = new JSONObject(format);		
		this.host = json.getString(TAG_HOST);
		this.ip = json.getString(TAG_IP);
		this.port = json.getInt(TAG_PORT);
		this.type = json.getString(TAG_TYPE_MESSAGE);
	}
}
