package uniandes.unacloud.common.net.udp.message;

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
	private static final String TAG_EXECUTIONS = "list_executions";

	/**
	 * Tag to keep Host User
	 */
	private static final String TAG_HOST_USER = "host_user";
	
	/**
	 * Tag to keep information about dataSpace
	 */
	private static final String TAG_DATA_SPACE = "data_space";
	
	/**
	 * Tag to keep information about freeSpace
	 */
	private static final String TAG_FREE_SPACE = "data_space";
	
	/**
	 * Tag to keep information about current agent version
	 */
	private static final String TAG_VERSION = "version";
	
	
	private Long[] executions; 
	
	private String hostUser;
	
	private Long freeSpace;
	
	private Long dataSpace;
	
	private String version;

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

		this.executions = executions;		
		this.hostUser = hostUser;
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

		this.freeSpace = freeSpace;		
		this.dataSpace = dataSpace;
		this.version = version;
		this.hostUser = hostUser;	
	}

	/**
	 * Creates a new UDP Message using a not null unacloud udp message
	 * @param message
	 */
	public MachineStateMessage(MachineStateMessage message) {
		setMessageByStringJson(message.getStringMessage());
	}

	/**
	 * Return the executions
	 * @return Long[] Executions
	 */
	public Long[] getExecutions() {
		return executions;
	}

	/**
	 * Return HostUser included in the Message
	 * @return String HostUser
	 */
	public String getHostUser() {
		return hostUser;
	}
	
	/**
	 * Returns dataSpace value included in the message
	 * @return dataSpace in bytes
	 */
	public Long getDataSpace() {
		return dataSpace;
	}
	
	/**
	 * Returns freeSpace value included in the message
	 * @return freeSpace in bytes
	 */
	public Long getFreeSpace() {
		return freeSpace;
	}
	
	/**
	 * Return current version agent in message
	 * @return current agent version
	 */
	public String getVersion() {
		return version;
	}
	
	@Override
	public void setMessageByStringJson(String format) {
		super.setMessageByStringJson(format);
		JSONObject json;
		json = new JSONObject(format);	
		this.freeSpace = json.getLong(TAG_FREE_SPACE);		
		this.dataSpace = json.getLong(TAG_DATA_SPACE);
		this.version = json.getString(TAG_VERSION);
		this.hostUser = json.getString(TAG_HOST_USER);
		this.executions = (Long[]) json.get(TAG_EXECUTIONS);
	}
	
	@Override
	protected JSONObject getJsonMessage() {
		JSONObject obj = super.getJsonMessage();
		obj.put(TAG_EXECUTIONS, executions);
		obj.put(TAG_HOST_USER, hostUser);
		obj.put(TAG_VERSION, version);
		obj.put(TAG_DATA_SPACE, dataSpace);
		obj.put(TAG_FREE_SPACE, freeSpace);
		return obj;
	}
}
