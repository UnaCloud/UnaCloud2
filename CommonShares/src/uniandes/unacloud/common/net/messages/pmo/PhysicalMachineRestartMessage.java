package uniandes.unacloud.common.net.messages.pmo;

import uniandes.unacloud.common.net.messages.PhysicalMachineOperationMessage;

/**
 * Represents message to restart physical machine
 * @author Clouder
 *
 */

public class PhysicalMachineRestartMessage extends PhysicalMachineOperationMessage {
	
	private static final long serialVersionUID = -1777920929881348888L;
	
	public PhysicalMachineRestartMessage() {
		super(PM_RESTART);
	}
}