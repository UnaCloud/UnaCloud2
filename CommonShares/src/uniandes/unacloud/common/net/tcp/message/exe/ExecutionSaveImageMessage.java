package uniandes.unacloud.common.net.tcp.message.exe;

import org.json.JSONObject;

import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;

/**
 * Represents message to send current execution to server
 * @author CesarF
 *
 */
public class ExecutionSaveImageMessage extends ImageOperationMessage{

	
	private static final long serialVersionUID = 3147489071041260127L;
	
	public static final String TOKEN = "token_com";
	
	public static final String IMAGE_ID = "image_id";
	
	public ExecutionSaveImageMessage(String ip, int port, String host,
			long executionId, String token, long imageId) {
		super(ip, port, host, ImageOperationMessage.VM_SAVE_IMG, executionId);
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TOKEN, token);
		tempMessage.put(IMAGE_ID, imageId);
		this.setMessage(tempMessage);	
	}

	public long getImageId() {
		return this.getMessage().getLong(IMAGE_ID);
	}
	
	public String getTokenCom() {
		return this.getMessage().getString(TOKEN);
	}
}