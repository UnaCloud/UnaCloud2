package communication.messages.udp;

import org.json.JSONObject;

import communication.UDPMessageEnum;

/**
 * Class to represent an UDP Message Type Log Physical Machine
 * @author cdsbarrera
 *
 */
public class UDPMessageLogPM extends UnaCloudMessageUDP{

	/**
	 * Serial Version UID Serialize
	 */
	private static final long serialVersionUID = 9215093125887679330L;

	/**
	 * Tag to keep information about executions
	 */
	public static final String TAG_COMPONENT = "component";
	
	/**
	 * Tag to keep Host User
	 */
	public static final String TAG_LOG_MESSAGE = "log_message";
	
	
	public UDPMessageLogPM(){
		
	}
	
	public UDPMessageLogPM(String ip, int port, String host, String component, String logMessage){
		super(ip, port, host, UDPMessageEnum.LOG_PM);
		
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TAG_COMPONENT, component);
		tempMessage.put(TAG_LOG_MESSAGE, logMessage);
		this.setMessage(tempMessage);
		
	}
	
	public UDPMessageLogPM(UnaCloudMessageUDP message) {
		super(message.getIp(), message.getPort(), message.getHost(), message.getType());
		this.setMessage(message.getMessage());	
	}
	
	/**
	 * Return the Component
	 * @return String Component
	 */
	public String getComponent(){
		return this.getMessage().getString(TAG_COMPONENT);
	}
	
	/**
	 * Return Log Message
	 * @return String Log Message
	 */
	public String getLogMessage(){
		try{
			return this.getMessage().getString(TAG_LOG_MESSAGE);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
