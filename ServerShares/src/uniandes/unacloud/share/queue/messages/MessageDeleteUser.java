package uniandes.unacloud.share.queue.messages;

import org.json.JSONObject;

import uniandes.unacloud.share.enums.QueueMessageType;


/**
 * Class used to represent the message to Delete User 
 * @author cdsbarrera
 * 
 */
public class MessageDeleteUser extends QueueMessage {
	
	private final static String TAG_ID_USER = "id_user";
	
	public MessageDeleteUser(String requester, long idUser) {
		super(requester);
		this.setType(QueueMessageType.DELETE_USER);
		
		JSONObject temp = this.getMessageContent();
		
		temp.put(TAG_ID_USER, idUser);
		
		this.setMessageContent(temp);
	}

	public MessageDeleteUser(QueueMessage message) {
		super(message.getRequester());
		this.setType(message.getType());
		this.setMessageContent(message.getMessageContent());
	}

	/**
	 * Return the Id of User
	 * @return Long Id User
	 */
	public Long getIdUser() {
		JSONObject temp = this.getMessageContent();
		try{
			return temp.getLong(TAG_ID_USER);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
