package uniandes.unacloud.common.net.messages;


/**
 * Represents kind of execution operation message sent to agents
 * @author CesarF
 *
 */
public abstract class ImageOperationMessage extends UnaCloudAbstractMessage {
	
	private static final long serialVersionUID = -719111911251582119L;
	
	public static final int VM_START = 1;
	
    public static final int VM_STOP = 2;
    
    public static final int VM_RESTART = 3;
    
    public static final int VM_STATE = 4;
    
    public static final int VM_TIME = 5;
    
    public static final int VM_HOST_TABLE = 6;
    
    public static final int VM_SAVE_IMG = 7;
    
    protected long executionId;
    
	public ImageOperationMessage(int subOperation) {
		super(EXECUTION_OPERATION,subOperation);
	}
	
	public long getExecutionId() {
		return executionId;
	}
	
	public void setExecutionId(long executionId) {
		this.executionId = executionId;
	}
	
}