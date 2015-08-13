package communication.messages.pmo;

import communication.messages.PhysicalMachineOperationMessage;

public class PhysicalMachineRestartMessage extends PhysicalMachineOperationMessage{
	private static final long serialVersionUID = -1777920929881348888L;
	public PhysicalMachineRestartMessage() {
		super(PM_RESTART);
	}
}
