package uniandes.unacloud.common.net.tcp.message;

import org.json.JSONObject;

import uniandes.unacloud.common.net.UnaCloudMessage;

/**
 * Represents type of tasks for agent
 * @author CesarF
 *
 */
public class AgentMessage extends UnaCloudMessage {
	
	private static final long serialVersionUID = 2295996510707565731L;
	
	/**
	 * Request to update agent files in physical machine
	 */
	public static final int UPDATE_OPERATION = 4;
	/**
	 * Request to stop agent 
	 */
	public static final int STOP_CLIENT = 6;
	/**
	 * Request to return version of agent
	 */
	public static final int GET_VERSION = 7;
	/**
	 * Request to clear data path 
	 */
	public static final int CLEAR_CACHE = 8;
	/**
	 * Request to delete one image form data path
	 */
	public static final int CLEAR_IMAGE_FROM_CACHE = 9;
	/**
	 * Request free space in data path
	 */
	public static final int GET_DATA_SPACE = 11;
	
	
	public static final String TYPE_TASK = "task";
	
	public static final String PM_ID = "pm_id";
	
	/**
	 * Creates a new Agent message
	 * @param subOperation
	 */
	public AgentMessage(String ip, int port, String host, int task, long pmId) {
		super(ip, port, host, TCPMessageEnum.AGENT_OPERATION.name());		
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
