package uniandes.unacloud.common.net.tcp.message;

import org.json.JSONObject;

import uniandes.unacloud.common.net.UnaCloudMessage;


/**
 * Represents kinf of physical machine operation message sent to agents
 * @author CesarF
 *
 */
public class PhysicalMachineOperationMessage extends UnaCloudMessage {
	
	private static final long serialVersionUID = 6749899457514674239L;
	
	public static final int PM_INIT = 1;
	
    public static final int PM_TURN_OFF = 1;
    
    public static final int PM_RESTART = 2;
    
    public static final int PM_LOGOUT = 3;
    
    public static final int PM_MONITOR = 4;
    
    public static final int PM_WRITE_FILE = 6;
    
    public static final int PM_TURN_ON = 7;
    
    public static final int PM_RETRIEVE_FOLDER = 8;
    
    public static final String TYPE_TASK = "task";
    
    public PhysicalMachineOperationMessage(String ip, int port, String host, int task) {
    	super(ip, port, host, TCPMessageEnum.PHYSICAL_MACHINE_OPERATION.name());		
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TYPE_TASK, task);
		this.setMessage(tempMessage);	
	}
    
    /**
	 * Return the task number
	 * @return String Component
	 */
	public int getTask() {
		return this.getMessage().getInt(TYPE_TASK);
	}
	
}
