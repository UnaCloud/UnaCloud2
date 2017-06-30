package uniandes.unacloud.share.queue.messages;

import org.json.JSONObject;

import uniandes.unacloud.share.enums.QueueMessageType;


/**
 * Class used to represent the message to Create Copy from Execution 
 * @author cdsbarrera
 * 
 */
public class MessageCreateCopyFromExecution extends QueueMessage{
	
	private final static String TAG_ID_EXECUTION = "id_execution";
	
	private final static String TAG_ID_IMAGE = "id_image";
	
	private final static String TAG_PAST_ID_IMAGE = "past_id_image";
	
	public MessageCreateCopyFromExecution(String requester, long idExecution, long idImage, long pastIdImage){
		super(requester);
		this.setType(QueueMessageType.CREATE_COPY);
		
		JSONObject temp = this.getMessageContent();
		
		temp.put(TAG_ID_EXECUTION, idExecution);
		temp.put(TAG_ID_IMAGE, idImage);
		temp.put(TAG_PAST_ID_IMAGE, pastIdImage);
		
		this.setMessageContent(temp);
	}
	
	public MessageCreateCopyFromExecution(QueueMessage message) {
		super(message.getRequester());
		this.setType(message.getType());
		this.setMessageContent(message.getMessageContent());
	}
	
	/**
	 * Return the Id of Execution
	 * @return Long Id Execution
	 */
	public Long getIdExecution() {
		JSONObject temp = this.getMessageContent();
		try {
			return temp.getLong(TAG_ID_EXECUTION);			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Return the Id of Image
	 * @return Long Id Image
	 */
	public Long getIdImage() {
		JSONObject temp = this.getMessageContent();
		try {
			return temp.getLong(TAG_ID_IMAGE);			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Return the Id of Past Image
	 * @return Long Id Past Image
	 */
	public Long getIdPastImage() {
		JSONObject temp = this.getMessageContent();
		try {
			return temp.getLong(TAG_PAST_ID_IMAGE);			
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
