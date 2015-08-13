package communication.messages;

import communication.UnaCloudAbstractMessage;
import communication.UnaCloudMessage;

public abstract class VirtualMachineOperationMessage extends UnaCloudAbstractMessage{
	private static final long serialVersionUID = -719111911251582119L;
	public static final int VM_START = 1;
    public static final int VM_STOP = 2;
    public static final int VM_RESTART = 3;
    public static final int VM_STATE = 4;
    public static final int VM_TIME = 5;
    public static final int VM_HOST_TABLE = 6;
    public static final int VM_SAVE_IMG = 7;
    
    protected long virtualMachineExecutionId;
	public VirtualMachineOperationMessage(int subOperation){
		super(VIRTUAL_MACHINE_OPERATION,subOperation);
	}
	public static UnaCloudAbstractMessage fromMessage(UnaCloudMessage message){
		return null;
	}
	public long getVirtualMachineExecutionId() {
		return virtualMachineExecutionId;
	}
	public void setVirtualMachineExecutionId(long virtualMachineExecutionId) {
		this.virtualMachineExecutionId = virtualMachineExecutionId;
	}
	
}