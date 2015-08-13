package communication.messages.vmo;

import communication.messages.VirtualMachineOperationMessage;

public class VirtualMachineUpdateHostTableMessage extends VirtualMachineOperationMessage{
	private static final long serialVersionUID = 8681348451860676292L;
	public VirtualMachineUpdateHostTableMessage(){
		super(VM_HOST_TABLE);
	}
	HostTable table;
	
}
