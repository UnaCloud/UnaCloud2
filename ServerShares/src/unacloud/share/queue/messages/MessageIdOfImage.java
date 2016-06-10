package unacloud.share.queue.messages;

import org.json.JSONObject;

import unacloud.share.enums.QueueMessageType;

public class MessageIdOfImage extends QueueMessage{

	private static final String TAG_ID_IMAGE = "id_image";
	
	public MessageIdOfImage(){
		super();
	}
	
	public MessageIdOfImage(QueueMessageType type, String requester, long idImage){
		super(requester);
		this.setType(type);
		
		JSONObject temp = this.getMessageContent();
		temp.put(TAG_ID_IMAGE, idImage);
		this.setMessageContent(temp);
	}
	
	public MessageIdOfImage(QueueMessage message) {
		super(message.getRequester());
		this.setType(message.getType());
		this.setMessageContent(message.getMessageContent());
	}
	
	/**
	 * Return the Number
	 * @return
	 */
	public long getIdImage() {
		JSONObject temp = this.getMessageContent();
		return temp.getLong(TAG_ID_IMAGE);
	}
}
