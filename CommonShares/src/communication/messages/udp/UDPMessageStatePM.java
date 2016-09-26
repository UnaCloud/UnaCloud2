package communication.messages.udp;

import org.json.JSONArray;
import org.json.JSONObject;

import communication.UDPMessageEnum;

/**
 * Class to represent an UDP Message Type State Physical Machine
 * @author cdsbarrera
 *
 */
public class UDPMessageStatePM extends UnaCloudMessageUDP{

	/**
	 * Serial Version UID Serialize 
	 */
	private static final long serialVersionUID = -414908833924500630L;

	/**
	 * Tag to keep information about executions
	 */
	public static final String TAG_EXECUTIONS = "list_executions";

	/**
	 * Tag to keep Host User
	 */
	public static final String TAG_HOST_USER = "host_user";


	public UDPMessageStatePM(){

	}

	public UDPMessageStatePM(String ip, int port, String host, String hostUser, Long[] executions){
		super(ip, port, host, UDPMessageEnum.STATE_PM);


		JSONArray arrayExecutions = new JSONArray();
		if(executions!=null) {
			for (int i = 0; i < executions.length; i++) {
				arrayExecutions.put(executions[i]);
			}
		}
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TAG_EXECUTIONS, arrayExecutions);
		tempMessage.put(TAG_HOST_USER,hostUser);
		this.setMessage(tempMessage);

	}

	public UDPMessageStatePM(UnaCloudMessageUDP message) {
		super(message.getIp(), message.getPort(), message.getHost(), message.getType());
		this.setMessage(message.getMessage());		
	}

	/**
	 * Return the executions
	 * @return Long[] Executions
	 */
	public Long[] getExecutions(){
		JSONObject temp = this.getMessage();
		JSONArray array = temp.getJSONArray(TAG_EXECUTIONS);
		Long[] toReturn = new Long[array.length()];
		for (int i = 0; i < array.length(); i++) {
			toReturn[i] = array.getLong(i);
		}
		return toReturn;
	}

	/**
	 * Return HostUser included in the Message
	 * @return String HostUser
	 */
	public String getHostUser(){
		try{
			return this.getMessage().getString(TAG_HOST_USER);
		} catch(Exception e){
			return null;
		}
	}
}
