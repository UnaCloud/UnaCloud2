package uniandes.unacloud.common.com.messages.agent;

import uniandes.unacloud.common.com.messages.AgentMessage;

/**
 * Represents message to clear image from folder cache in agents
 * @author Clouder
 *
 */
public class ClearImageFromCacheMessage extends AgentMessage{

	private static final long serialVersionUID = 524061116935661249L;
	private long imageId;
	public ClearImageFromCacheMessage(long imageId) {
		super(CLEAR_IMAGE_FROM_CACHE);
		this.imageId=imageId;
	}
	public long getImageId() {
		return imageId;
	}
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}
}
