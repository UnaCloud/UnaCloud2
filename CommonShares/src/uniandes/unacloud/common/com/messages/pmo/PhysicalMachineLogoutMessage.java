package uniandes.unacloud.common.com.messages.pmo;

import uniandes.unacloud.common.com.messages.PhysicalMachineOperationMessage;

/**
 * Represents message to logout physical machine
 * @author Clouder
 *
 */
public class PhysicalMachineLogoutMessage extends PhysicalMachineOperationMessage{
	private static final long serialVersionUID = -3101439160990637049L;
	public PhysicalMachineLogoutMessage() {
		super(PM_LOGOUT);
	}
}
