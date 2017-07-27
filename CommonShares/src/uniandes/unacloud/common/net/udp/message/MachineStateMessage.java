package uniandes.unacloud.common.net.udp.message;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.unacloud.common.net.UnaCloudMessage;

/**
 * Class to represent an UDP Message Type State Physical Machine
 * @author cdsbarrera
 *
 */
public class MachineStateMessage extends UnaCloudMessage {

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
	
	/**
	 * Tag to keep information about dataSpace
	 */
	public static final String TAG_DATA_SPACE = "data_space";
	
	/**
	 * Tag to keep information about freeSpace
	 */
	public static final String TAG_FREE_SPACE = "data_space";
	
	/**
	 * Tag to keep information about current agent version
	 */
	public static final String TAG_VERSION = "version";


	public MachineStateMessage(){

	}
	
	/**
	 * Creates a new UDP message with basic information to report node state
	 * @param ip IP server address 
	 * @param port in server
	 * @param host current hostname in physical machine
	 * @param hostUser current user executing processes in physical machine
	 * @param executions list of current executions in machine
	 */
	public MachineStateMessage(String ip, int port, String host, String hostUser, Long[] executions) {
		super(ip, port, host, UDPMessageEnum.STATE_PM.name());


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
	
	/**
	 * Creates a new UDP message with extra information to report node state
	 * @param ip IP server address 
	 * @param port in server
	 * @param host current hostname in physical machine
	 * @param hostUser current user executing processes in physical machine
	 * @param dataSpace quantity of total bytes in data path
	 * @param freeSpace quantity of free bytes in data path
	 * @param version current agent version
	 */
	public MachineStateMessage(String ip, int port, String host, String hostUser, Long freeSpace, Long dataSpace, String version) {
		super(ip, port, host, UDPMessageEnum.STATE_PM.name());

		JSONObject tempMessage = this.getMessage();
		if (version != null) 
			tempMessage.put(TAG_VERSION, version);
		tempMessage.put(TAG_HOST_USER,hostUser);
		tempMessage.put(TAG_DATA_SPACE,dataSpace);
		tempMessage.put(TAG_FREE_SPACE,freeSpace);
		this.setMessage(tempMessage);

	}

	/**
	 * Creates a new UDP Message using a not null unacloud udp message
	 * @param message
	 */
	public MachineStateMessage(UnaCloudMessage message) {
		super(message.getIp(), message.getPort(), message.getHost(), message.getType());
		this.setMessage(message.getMessage());		
	}

	/**
	 * Return the executions
	 * @return Long[] Executions
	 */
	public Long[] getExecutions() {
		JSONObject temp = this.getMessage();
		try {
			JSONArray array = temp.getJSONArray(TAG_EXECUTIONS);
			Long[] toReturn = new Long[array.length()];
			for (int i = 0; i < array.length(); i++) {
				toReturn[i] = array.getLong(i);
			}
			return toReturn;
		} catch (Exception e) {
			return null;
		}	
	}

	/**
	 * Return HostUser included in the Message
	 * @return String HostUser
	 */
	public String getHostUser() {
		try{
			return this.getMessage().getString(TAG_HOST_USER);
		} catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Returns dataSpace value included in the message
	 * @return dataSpace in bytes
	 */
	public Long getDataSpace() {
		try {
			return this.getMessage().getLong(TAG_DATA_SPACE);
		} catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns freeSpace value included in the message
	 * @return freeSpace in bytes
	 */
	public Long getFreeSpace() {
		try {
			return this.getMessage().getLong(TAG_FREE_SPACE);
		} catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Return current version agent in message
	 * @return current agent version
	 */
	public String getVersion() {
		try {
			return this.getMessage().getString(TAG_VERSION);
		} catch(Exception e) {
			return null;
		}
	}
}
