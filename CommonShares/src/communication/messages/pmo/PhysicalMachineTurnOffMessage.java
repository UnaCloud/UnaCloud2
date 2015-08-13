package communication.messages.pmo;

import communication.messages.PhysicalMachineOperationMessage;

public class PhysicalMachineTurnOffMessage extends PhysicalMachineOperationMessage{
	private static final long serialVersionUID = 9194059152159271108L;
	public PhysicalMachineTurnOffMessage() {
		super(PM_TURN_OFF);
	}
}
