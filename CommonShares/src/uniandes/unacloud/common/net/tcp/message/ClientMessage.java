package uniandes.unacloud.common.net.tcp.message;

import org.json.JSONObject;

import uniandes.unacloud.common.net.UnaCloudMessage;

public class ClientMessage extends UnaCloudMessage {
	
	private static final long serialVersionUID = 457883070963170385L;

	private static final String TYPE_TASK = "task";
	
	private static final String PM_ID = "pm_id";
	
	private int task;
	
	private long pmId;
	
	/**
	 * Creates a new Agent message
	 * @param subOperation
	 */
	public ClientMessage (String ip, int port, String host, String type, int task, long pmId) {
		super(ip, port, host, type);	
		this.task = task;
		this.pmId = pmId;	
	}
	
	/**
	 * Return the task number
	 * @return String Component
	 */
	public int getTask() {
		return task;
	}
	
	/**
	 * Returns physical machine id
	 * @return
	 */
	public long getPmId() {
		return pmId;
	}
	
	@Override
	public void setMessageByStringJson(String format) {
		super.setMessageByStringJson(format);
		JSONObject json;
		json = new JSONObject(format);		
		this.task = json.getInt(TYPE_TASK);
		this.pmId = json.getLong(PM_ID);
	}
	
	@Override
	protected JSONObject getJsonMessage() {
		JSONObject obj = super.getJsonMessage();
		obj.put(TYPE_TASK, task);
		obj.put(PM_ID, pmId);
		return obj;
	}

}
