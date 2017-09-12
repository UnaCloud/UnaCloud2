package uniandes.unacloud.common.net.udp.message;

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
	
	private String component;
	
	private String logMessage;
	
	public MachineLogMessage() {
		
	}
	
	public MachineLogMessage(String ip, int port, String host, String component, String logMessage) {
		super(ip, port, host, UDPMessageEnum.LOG_PM.name());
		
		this.component = component;
		this.logMessage = logMessage;		
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
	public String toString() {
		return "MachineLogMessage [component=" + component + ", logMessage="
				+ logMessage + " - " + super.toString() + "]";
	}
	
	
}
