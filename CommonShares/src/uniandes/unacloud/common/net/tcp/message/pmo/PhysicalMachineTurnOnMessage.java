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
	
	private static final String MACS = "macs";
	
	private String[] macs;
	
	public PhysicalMachineTurnOnMessage(String ip, int port, String host, String[] macs, long pmId) {
		super(ip, port, host, PhysicalMachineOperationMessage.PM_TURN_ON, pmId);
		this.macs = macs;
	}
	
	public String[] getMacs() {
		return macs;
	}
	
	@Override
	public void setMessageByStringJson(String format) {
		super.setMessageByStringJson(format);
		JSONObject json;
		json = new JSONObject(format);		
		this.macs = (String[])json.get(MACS);
	}
	
	@Override
	protected JSONObject getJsonMessage() {
		JSONObject obj = super.getJsonMessage();
		obj.put(MACS, macs);
		return obj;
	}
}
