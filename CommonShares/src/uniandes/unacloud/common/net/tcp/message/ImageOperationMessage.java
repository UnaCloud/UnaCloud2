package uniandes.unacloud.common.net.tcp.message;

import org.json.JSONObject;

import uniandes.unacloud.common.net.UnaCloudMessage;


/**
 * Represents kind of execution operation message sent to agents
 * @author CesarF
 *
 */
public class ImageOperationMessage extends UnaCloudMessage {
	
	private static final long serialVersionUID = -719111911251582119L;
	
	public static final int VM_START = 1;
	
    public static final int VM_STOP = 2;
    
    public static final int VM_RESTART = 3;
    
    public static final int VM_STATE = 4;
    
    public static final int VM_TIME = 5;
    
    public static final int VM_HOST_TABLE = 6;
    
    public static final int VM_SAVE_IMG = 7;
    
    public static final String TYPE_TASK = "task";
    
    public static final String EXECUTION = "execution_id";
        
	public ImageOperationMessage(String ip, int port, String host, int task, long executionId) {
		super(ip, port, host, TCPMessageEnum.EXECUTION_OPERATION.name());		
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TYPE_TASK, task);
		tempMessage.put(EXECUTION, executionId);
		this.setMessage(tempMessage);	
	}
	
	public long getExecutionId() {
		return this.getMessage().getLong(EXECUTION);
	}
	
	public int getTask() {
		return this.getMessage().getInt(TYPE_TASK);
	}
	
}