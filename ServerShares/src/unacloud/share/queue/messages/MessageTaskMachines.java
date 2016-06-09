package unacloud.share.queue.messages;

import org.json.JSONArray;
import org.json.JSONObject;

import unacloud.share.enums.QueueMessageType;
import unacloud.share.enums.TaskEnum;
import unacloud.share.queue.QueueMessage;


/**
 * Class used to represent the message of Task in a list of Machines. 
 * @author cdsbarrera
 * 
 */
public class MessageTaskMachines extends QueueMessage{

	private final static String TAG_LIST_MACHINES_ID = "list_machines";
	private final static String TAG_TASK = "task";
	
	public MessageTaskMachines(String requester, long[] machines, String task){
		super(requester);
		this.setType(QueueMessageType.SEND_TASK);
		
		JSONObject temp = this.getMessageContent();
		
		JSONArray array = new JSONArray();
		for (int i = 0; i < machines.length; i++) {
			array.put(machines[i]);
		}
		temp.put(TAG_TASK, task);
		temp.put(TAG_LIST_MACHINES_ID, array);
		
		this.setMessageContent(temp);
	}
	
	/**
	 * Return the Task in class TaskEnum
	 * @return TaskEnum
	 */
	public TaskEnum getTask() {
		JSONObject temp = this.getMessageContent();
		return TaskEnum.getEnum(temp.getString(TAG_TASK));
	}

	/**
	 * Return Iterator on list of Ids
	 * @return Iterator
	 */
	public Long[] getIdMachines() {
		JSONObject temp = this.getMessageContent();
		JSONArray list = temp.getJSONArray(TAG_TASK);
		Long[] array = new Long[list.length()];
		for (int i = 0; i < list.length(); i++) {
			array[i] = list.getLong(i);
		}
		return array;
	}
}
