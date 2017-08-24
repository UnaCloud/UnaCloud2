package uniandes.unacloud.common.net.udp.message;

import org.json.JSONObject;

import uniandes.unacloud.common.net.UnaCloudMessage;

/**
 * Class to represent an UDP Message Type Log Physical Machine
 * @author cdsbarrera, CesarF
 *
 */
public class MachineLogMessage extends UnaCloudMessage {

	/**
	 * Serial Version UID Serialize
	 */
	private static final long serialVersionUID = 9215093125887679330L;

	/**
	 * Tag to keep information about executions
	 */
	private static final String TAG_COMPONENT = "component";
	
	/**
	 * Tag to keep Host User
	 */
	private static final String TAG_LOG_MESSAGE = "log_message";
	
	private String component;
	
	private String logMessage;
	
	public MachineLogMessage() {
		
	}
	
	public MachineLogMessage(String ip, int port, String host, String component, String logMessage) {
		super(ip, port, host, UDPMessageEnum.LOG_PM.name());
		
		this.component = component;
		this.logMessage = logMessage;		
	}
	
	public MachineLogMessage(MachineLogMessage message) {
		setMessageByStringJson(message.getStringMessage());
	}
	
	/**
	 * Return the Component
	 * @return String Component
	 */
	public String getComponent() {
		return component;
	}
	
	/**
	 * Return Log Message
	 * @return String Log Message
	 */
	public String getLogMessage() {
		return logMessage;
	}
	
	@Override
	public void setMessageByStringJson(String format) {
		super.setMessageByStringJson(format);
		JSONObject json;
		json = new JSONObject(format);		
		this.component = json.getString(TAG_COMPONENT);
		this.logMessage = json.getString(TAG_LOG_MESSAGE);
	}
	
	@Override
	protected JSONObject getJsonMessage() {
		JSONObject obj = super.getJsonMessage();
		obj.put(TAG_COMPONENT, component);
		obj.put(TAG_LOG_MESSAGE, logMessage);
		return obj;
	}
	
}
