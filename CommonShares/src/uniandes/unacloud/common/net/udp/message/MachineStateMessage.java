package uniandes.unacloud.common.net.udp.message;


import java.util.Arrays;

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
	public String toString() {
		return "MachineStateMessage [executions=" + Arrays.toString(executions)
				+ ", hostUser=" + hostUser + ", freeSpace=" + freeSpace
				+ ", dataSpace=" + dataSpace + ", version=" + version + " - " + super.toString() + "]";
	}	
	
}
