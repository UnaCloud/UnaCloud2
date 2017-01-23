package uniandes.unacloud.common.com.messages.pmo;

import uniandes.unacloud.common.com.messages.PhysicalMachineOperationMessage;


/**
 * Represents message to turn on physical machine
 * @author Clouder
 *
 */
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
