package uniandes.unacloud.common.net.tcp.message.pmo;

import uniandes.unacloud.common.net.tcp.message.PhysicalMachineOperationMessage;


/**
 * Represents message to turn on physical machine
 * @author Clouder
 *
 */
public class PhysicalMachineTurnOnMessage extends PhysicalMachineOperationMessage {
	
	
	private static final long serialVersionUID = -7026046062306316388L;
		
	private String[] macs;
	
	public PhysicalMachineTurnOnMessage(String ip, int port, String host, String[] macs, long pmId) {
		super(ip, port, host, PhysicalMachineOperationMessage.PM_TURN_ON, pmId);
		this.macs = macs;
	}
	
	public String[] getMacs() {
		return macs;
	}
	
}
