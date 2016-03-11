package communication.messages.vmo;

import communication.messages.VirtualMachineOperationMessage;

/**
 * Represents message to stop a virtual machine execution
 * @author CesarF
 *
 */
public class VirtualMachineStopMessage extends VirtualMachineOperationMessage{
	private static final long serialVersionUID = -8728929759121643688L;
	public VirtualMachineStopMessage() {
		super(VM_STOP);
	}
}
