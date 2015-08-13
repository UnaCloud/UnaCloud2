package communication.messages.pmo;

import communication.messages.PhysicalMachineOperationMessage;

public class PhysicalMachineTurnOnMessage extends PhysicalMachineOperationMessage{
	private static final long serialVersionUID = -7026046062306316388L;
	String[] macs;
	public PhysicalMachineTurnOnMessage() {
		super(PM_TURN_ON);
	}
	public String[] getMacs() {
		return macs;
	}
}
