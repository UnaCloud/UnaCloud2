package uniandes.unacloud.common.net.tcp.message;

import org.json.JSONObject;

import uniandes.unacloud.common.net.UnaCloudMessage;

public class ClientMessage extends UnaCloudMessage {
	
	private static final long serialVersionUID = 457883070963170385L;

	public static final String TYPE_TASK = "task";
	
	public static final String PM_ID = "pm_id";
	
	/**
	 * Creates a new Agent message
	 * @param subOperation
	 */
	public ClientMessage (String ip, int port, String host, String type, int task, long pmId) {
		super(ip, port, host, type);	
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TYPE_TASK, task);
		tempMessage.put(PM_ID, pmId);
		this.setMessage(tempMessage);		
	}
	
	/**
	 * Return the task number
	 * @return String Component
	 */
	public int getTask() {
		return this.getMessage().getInt(TYPE_TASK);
	}
	
	public long getPmId() {
		return this.getMessage().getLong(PM_ID);
	}

}
