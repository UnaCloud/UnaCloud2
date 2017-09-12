package uniandes.unacloud.share.queue.messages;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.share.enums.QueueMessageType;


/**
 * Class used to represent the message of Add Instances to Deploy. 
 * @author cdsbarrera
 * 
 */
public class MessageAddInstances extends QueueMessage {
	
	private final static String TAG_LIST_EXECUTIONS = "list_executions";
	
	private final static String TAG_ID_IMAGE = "id_image";
	
	private static final String TAG_TRANSMISSION = "transmission_type";

	public MessageAddInstances(String requester, Long idImage, Long[] executions, TransmissionProtocolEnum transType) {
		super(requester);
		this.setType(QueueMessageType.ADD_INSTANCES);

		JSONObject temp = this.getMessageContent();

		JSONArray array = new JSONArray();
		if (executions != null)
			for (int i = 0; i < executions.length; i++)
				array.put(executions[i]);			
		
		temp.put(TAG_ID_IMAGE, idImage);
		temp.put(TAG_LIST_EXECUTIONS, array);
		temp.put(TAG_TRANSMISSION, transType);

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
		for (int i = 0; i < list.length(); i++) 
			array[i] = list.getLong(i);		
		return array;
	}
	
	public TransmissionProtocolEnum getTypeTransmission() {
		JSONObject temp = this.getMessageContent();
		try {
			return TransmissionProtocolEnum.getEnum(temp.getString(TAG_TRANSMISSION));
		} catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
}
