package uniandes.unacloud.common.net.tcp.message;


/**
 * Represents kind of execution operation message sent to agents
 * @author CesarF
 *
 */
public class ImageOperationMessage extends ClientMessage {
	
	private static final long serialVersionUID = -719111911251582119L;
	
	public static final int VM_START = 1;
	
	public static final int VM_STOP = 2;
    
	public static final int VM_RESTART = 3;
    
	public static final int VM_STATE = 4;
    
	public static final int VM_TIME = 5;
    
	public static final int VM_HOST_TABLE = 6;
    
	public static final int VM_SAVE_IMG = 7;
        
	public static final String EXECUTION = "execution_id";
    
    private long executionId;
        
	public ImageOperationMessage(String ip, int port, String host, int task, long pmId, long executionId) {
		super(ip, port, host, TCPMessageEnum.EXECUTION_OPERATION.name(), task, pmId);		
		this.executionId = executionId;	
	}
	
	public long getExecutionId() {
		return this.executionId;
	}

	@Override
	public String toString() {
		return "ImageOperationMessage [executionId = " + executionId + "] " + super.toString();
	}
	
	
}