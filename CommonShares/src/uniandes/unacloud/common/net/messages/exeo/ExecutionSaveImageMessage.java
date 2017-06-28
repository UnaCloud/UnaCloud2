package uniandes.unacloud.common.net.messages.exeo;

import uniandes.unacloud.common.net.messages.ImageOperationMessage;

/**
 * Represents message to send current execution to server
 * @author CesarF
 *
 */
public class ExecutionSaveImageMessage extends ImageOperationMessage{

	private long imageId;
	
	private String tokenCom;
	
	private static final long serialVersionUID = 3147489071041260127L;
	
	public ExecutionSaveImageMessage() {
		super(VM_SAVE_IMG);
	}

	public long getImageId() {
		return imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
	}
	
	public String getTokenCom() {
		return tokenCom;
	}

	public void setTokenCom(String tokenCom) {
		this.tokenCom = tokenCom;
	}

}