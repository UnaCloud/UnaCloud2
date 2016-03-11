package communication.messages.vmo;

import communication.messages.VirtualMachineOperationMessage;


/**
 * Represents message to restart physical machine
 * @author Clouder
 *
 */
public class VirtualMachineRestartMessage extends VirtualMachineOperationMessage{
	private static final long serialVersionUID = 619421995819548819L;
	public VirtualMachineRestartMessage() {
		super(VM_RESTART);
	}
}