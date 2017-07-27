package uniandes.unacloud.common.net.tcp.message.agent;

import org.json.JSONObject;

import uniandes.unacloud.common.net.tcp.message.AgentMessage;


/**
 * Represents message to clear image from folder cache in agents
 * @author Clouder
 *
 */
public class ClearImageFromCacheMessage extends AgentMessage {

	private static final long serialVersionUID = 524061116935661249L;
	
	private static final String CLEAR_IMAGE_ID = "clear_image_id";

	public ClearImageFromCacheMessage(String ip, int port, String host, long pmId, long imageId) {
		super(ip, port, host, AgentMessage.CLEAR_IMAGE_FROM_CACHE, pmId);
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(CLEAR_IMAGE_ID, imageId);
		this.setMessage(tempMessage);		
	}
	
	/**
	 * Return the Component
	 * @return String Component
	 */
	public long getImageId() {
		return this.getMessage().getLong(CLEAR_IMAGE_ID);
	}
}
