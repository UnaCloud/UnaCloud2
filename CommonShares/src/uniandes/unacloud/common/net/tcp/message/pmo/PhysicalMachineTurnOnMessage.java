package uniandes.unacloud.common.net.tcp.message.pmo;

import org.json.JSONObject;

import uniandes.unacloud.common.net.tcp.message.PhysicalMachineOperationMessage;


/**
 * Represents message to turn on physical machine
 * @author Clouder
 *
 */
public class PhysicalMachineTurnOnMessage extends PhysicalMachineOperationMessage {
	
	
	private static final long serialVersionUID = -7026046062306316388L;
	
	public static final String MACS = "macs";
	
	public PhysicalMachineTurnOnMessage(String ip, int port, String host, String[] macs) {
		super(ip, port, host, PhysicalMachineOperationMessage.PM_TURN_ON);
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(MACS, macs);
		this.setMessage(tempMessage);	
	}
	
	public String[] getMacs() {
		return (String[]) this.getMessage().get(MACS);
	}
}
