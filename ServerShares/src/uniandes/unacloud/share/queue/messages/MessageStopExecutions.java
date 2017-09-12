package uniandes.unacloud.share.queue.messages;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.unacloud.share.enums.QueueMessageType;


/**
 * Class used to represent the message of Task in a list of Machines. 
 * @author cdsbarrera
 * 
 */
public class MessageStopExecutions extends QueueMessage {

	private final static String TAG_LIST_EXECUTIONS_ID = "list_executions";

	public MessageStopExecutions(String requester, Long[] executions) {
		super(requester);
		this.setType(QueueMessageType.STOP_DEPLOYS);

		JSONObject temp = this.getMessageContent();

		JSONArray array = new JSONArray();
		if (executions != null)
			for (int i = 0; i < executions.length; i++)
				array.put(executions[i]);
		
		temp.put(TAG_LIST_EXECUTIONS_ID, array);

		this.setMessageContent(temp);
	}

	public MessageStopExecutions(QueueMessage message) {
		super(message.getRequester());
		this.setType(message.getType());
		this.setMessageContent(message.getMessageContent());
	}

	/**
	 * Return long array of id's Executions
	 * @return Long[]
	 */
	public Long[] getIdExecutions() {
		JSONObject temp = this.getMessageContent();
		JSONArray list = temp.getJSONArray(TAG_LIST_EXECUTIONS_ID);
		Long[] array = new Long[list.length()];
		for (int i = 0; i < list.length(); i++)
			array[i] = list.getLong(i);
		return array;
	}
}
