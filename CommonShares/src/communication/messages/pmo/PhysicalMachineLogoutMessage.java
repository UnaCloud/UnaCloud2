package communication.messages.pmo;

import communication.messages.PhysicalMachineOperationMessage;

public class PhysicalMachineLogoutMessage extends PhysicalMachineOperationMessage{
	private static final long serialVersionUID = -3101439160990637049L;
	public PhysicalMachineLogoutMessage() {
		super(PM_LOGOUT);
	}
}
