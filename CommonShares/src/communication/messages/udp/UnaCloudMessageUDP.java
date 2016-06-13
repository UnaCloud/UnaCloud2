package communication.messages.udp;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import communication.UDPMessageEnum;

/**
 * Used as standard for send message in UDP Protocol
 * @author CesarF
 *
 */
public class UnaCloudMessageUDP implements Serializable{
	
	private static final long serialVersionUID = -8785226165731609571L;
	
	public static String TAG_HOST = "host";
	public static String TAG_IP = "ip";
	public static String TAG_PORT = "post";
	public static String TAG_TYPE_MESSAGE = "type_message";
	public static String TAG_MESSAGE = "message";
	
	private String host;
	private String ip;
	private int port;
	private UDPMessageEnum type;
	private JSONObject message; 
	
	public UnaCloudMessageUDP(){		
	}
	
	public UnaCloudMessageUDP(String ip, int port, String host, UDPMessageEnum type) {
		super();
		this.message = new JSONObject();
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
	
	public JSONObject getMessage() {
		return message;
	}

	public void setMessage(JSONObject message) {
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
	
	/**
	 * Return the String of JSON Message
	 * @return String
	 */
	public String getStringMessage(){
		JSONObject total = new JSONObject();
		
		total.put(TAG_HOST, this.host);
		total.put(TAG_IP, this.ip);
		total.put(TAG_PORT, this.port);
		total.put(TAG_TYPE_MESSAGE, this.type.name());
		total.put(TAG_MESSAGE, this.message);
		
		StringWriter out = new StringWriter();
		total.write(out);
		
		String jsonText = out.toString();
		return jsonText;
		
	}
	
	/**
	 * generates a byte array to send message based in components in message
	 * @return byte array
	 */
	public byte[] generateByteMessage(){
		String messageString = this.getStringMessage();
		return messageString.getBytes();
	}
	
	/**
	 * Transform a byte array in parts of message (JSON Format)
	 * @param bytes
	 * @throws UnsupportedEncodingException
	 */
	public void setMessageByBytes(byte[] bytes) throws UnsupportedEncodingException{
		String tempMessage = new String(bytes, "UTF-8");
		this.getMessageByString(tempMessage);
	}

	/**
	 * Read the String of a message and set with the variables.
	 * @param format
	 */
	public void getMessageByString(String format) {
		JSONObject json;
		json = new JSONObject(format);
		
		this.host = json.getString(TAG_HOST);
		this.ip = json.getString(TAG_IP);
		this.port = json.getInt(TAG_PORT);
		this.type = UDPMessageEnum.getType(json.getString(TAG_TYPE_MESSAGE));
		this.message = json.getJSONObject(TAG_MESSAGE);		
	}
}
