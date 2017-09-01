package uniandes.unacloud.common.net.tcp.message;

import uniandes.unacloud.common.net.UnaCloudMessage;

/**
 * 
 * @author CesarF
 *
 */
public class ClientMessage extends UnaCloudMessage {
	
	private static final long serialVersionUID = 457883070963170385L;
	
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
	public String toString() {
		return "ClientMessage [task=" + task + ", pmId=" + pmId + "]" + super.toString();
	}

}
