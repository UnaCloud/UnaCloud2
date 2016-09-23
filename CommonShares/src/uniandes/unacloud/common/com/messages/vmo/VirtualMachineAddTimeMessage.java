package uniandes.unacloud.common.com.messages.vmo;

import uniandes.unacloud.common.com.messages.VirtualMachineOperationMessage;
import uniandes.unacloud.common.utils.Time;


/**
 * Represents message to add time to execution
 * @author Clouder
 *
 */
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