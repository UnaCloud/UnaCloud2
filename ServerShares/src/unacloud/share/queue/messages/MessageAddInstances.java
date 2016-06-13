package unacloud.share.queue.messages;

import org.json.JSONArray;
import org.json.JSONObject;

import unacloud.share.enums.QueueMessageType;


/**
 * Class used to represent the message of Add Instances to Deploy. 
 * @author cdsbarrera
 * 
 */
public class MessageAddInstances extends QueueMessage{
	private final static String TAG_LIST_EXECUTIONS = "list_executions";
	private final static String TAG_ID_IMAGE = "id_image";

	public MessageAddInstances(String requester, Long idImage, Long[] executions){
		super(requester);
		this.setType(QueueMessageType.ADD_INSTANCES);

		JSONObject temp = this.getMessageContent();

		JSONArray array = new JSONArray();
		if(executions!=null) {
			for (int i = 0; i < executions.length; i++) {
				array.put(executions[i]);
			}
		}
		temp.put(TAG_ID_IMAGE, idImage);
		temp.put(TAG_LIST_EXECUTIONS, array);

		this.setMessageContent(temp);
	}

	public MessageAddInstances(QueueMessage message) {
		super(message.getRequester());
		this.setType(message.getType());
		this.setMessageContent(message.getMessageContent());
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
	 * Return list of Ids Executions
	 * @return Long[] array of Id's Executions
	 */
	public Long[] getIdExecutions() {
		JSONObject temp = this.getMessageContent();
		JSONArray list = temp.getJSONArray(TAG_LIST_EXECUTIONS);
		Long[] array = new Long[list.length()];
		for (int i = 0; i < list.length(); i++) {
			array[i] = list.getLong(i);
		}
		return array;
	}
}
