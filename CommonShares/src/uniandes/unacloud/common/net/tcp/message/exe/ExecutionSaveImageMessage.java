package uniandes.unacloud.common.net.tcp.message.exe;

import org.json.JSONObject;

import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;

/**
 * Represents message to send current execution to server
 * @author CesarF
 *
 */
public class ExecutionSaveImageMessage extends ImageOperationMessage {

	
	private static final long serialVersionUID = 3147489071041260127L;
	
	private static final String TOKEN = "token_com";
	
	private static final String IMAGE_ID = "image_id";
	
	private String token;
	
	private long imageId;
	
	public ExecutionSaveImageMessage(String ip, int port, String host,
			long executionId, long pmId, String token, long imageId) {
		super(ip, port, host, ImageOperationMessage.VM_SAVE_IMG, pmId, executionId);
		this.token = token;
		this.imageId = imageId;
	}

	public long getImageId() {
		return imageId;
	}
	
	public String getTokenCom() {
		return token;
	}
	
	@Override
	public void setMessageByStringJson(String format) {
		super.setMessageByStringJson(format);
		JSONObject json;
		json = new JSONObject(format);		
		this.token = json.getString(TOKEN);
		this.imageId = json.getLong(IMAGE_ID);
	}
	
	@Override
	protected JSONObject getJsonMessage() {
		JSONObject obj = super.getJsonMessage();
		obj.put(TOKEN, token);
		obj.put(IMAGE_ID, imageId);
		return obj;
	}
}