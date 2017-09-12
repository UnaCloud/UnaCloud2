package uniandes.unacloud.agent.net.send;

import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.net.udp.UDPSender;
import uniandes.unacloud.common.net.udp.message.MachineLogMessage;
import uniandes.unacloud.common.net.udp.message.MachineStateMessage;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Singleton class to send message to server using UDP protocol
 * @author CesarF
 *
 */
public class UDPCommunicator {
	

	/**
	 * Object to send udp messages
	 */
	private UDPSender sender;
	
	/**
	 * Singleton instance
	 */
	private static UDPCommunicator instance;
	
	/**
	 * Responsible to return instance from this class
	 * @return singleton instance 
	 */
	public static UDPCommunicator getInstance() {
		if (instance == null)
			instance = new UDPCommunicator();
		return instance;
	}
	
	/**
	 * Constructor method
	 */
	private UDPCommunicator() {
		sender = new UDPSender();
	}
	
	/**
	 * Push info by UDP protocol to server port for physical machine reports
	 * @param hostName : current hostname
	 * @param userName : name of current user on machine
	 * @param executions : list of ids from current executions
	 * @return true if message was sent, false in case not
	 * @throws Exception 
	 */
	public synchronized boolean pushInfoPM(String hostName, String userName, Long[] executions) throws Exception {
		String serverIP = VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_PM_PORT);
		MachineStateMessage message = new MachineStateMessage (serverIP, serverPort, hostName, userName, executions);		
		return sender.sendMessage(message);
	}
	
	/**
	 * Push info with extra information by UDP protocol to server port for physical machine reports 
	 * @param hostName : current hostname
	 * @param userName : name of current user on machine
	 * @param version : current agent version
	 * @param freeSpace : current free space in data path
	 * @param dataSpace : current total space in data path
	 * @return true if message was sent, false in case not
	 * @throws Exception 
	 */
	public synchronized boolean pushInfoPM(String hostName, String userName, Long freeSpace, Long dataSpace, String version) throws Exception {
		String serverIP=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_PM_PORT);
		MachineStateMessage message = new MachineStateMessage(serverIP, serverPort, hostName, userName, freeSpace, dataSpace, version);		
		return sender.sendMessage(message);
	}	
	
	/**
	 * Push Info by UDP Protocol to server port for Log in Physical Machines
	 * @param hostName Sender
	 * @param component
	 * @param logMessage
	 * @return true if message was sent, false in case not
	 * @throws Exception
	 */
	public synchronized boolean pushInfoLogPM(String hostName, String component, String logMessage) throws Exception {
		String serverIP = VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT);
		MachineLogMessage message = new MachineLogMessage(serverIP, serverPort, hostName, component, logMessage);
		return sender.sendMessage(message);
	}
	
}
