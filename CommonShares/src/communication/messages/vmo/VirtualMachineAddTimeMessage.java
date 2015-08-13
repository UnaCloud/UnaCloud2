package communication.messages.vmo;

import com.losandes.utils.Time;

import communication.messages.VirtualMachineOperationMessage;

public class VirtualMachineAddTimeMessage extends VirtualMachineOperationMessage{
	private static final long serialVersionUID = 7525891768407688888L;
	Time executionTime;
    String id;
    public VirtualMachineAddTimeMessage() {
		super(VM_TIME);
	}
	public Time getExecutionTime() {
		return executionTime;
	}
	public String getId() {
		return id;
	}
}