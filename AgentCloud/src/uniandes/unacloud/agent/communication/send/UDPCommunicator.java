package uniandes.unacloud.agent.communication.send;

import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.com.UnaCloudDataSenderUDP;
import uniandes.unacloud.common.com.messages.udp.UDPMessageLogPM;
import uniandes.unacloud.common.com.messages.udp.UDPMessageStatePM;
import uniandes.unacloud.common.com.messages.udp.UDPMessageStateEXE;
import uniandes.unacloud.common.enums.ExecutionStateEnum;
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
	private UnaCloudDataSenderUDP sender;
	
	/**
	 * Singleton instance
	 */
	private static UDPCommunicator instance;
	
	/**
	 * Responsible to return instance from this class
	 * @return singleton instance 
	 */
	public static UDPCommunicator getInstance(){
		if(instance == null)instance = new UDPCommunicator();
		return instance;
	}
	
	/**
	 * Constructor method
	 */
	private UDPCommunicator() {
		sender = new UnaCloudDataSenderUDP();
	}
	
	/**
	 * Push info by UDP protocol to server port for physical machine reports
	 * @param hostName : current hostname
	 * @param userName : name of current user on machine
	 * @param executions : list of ids from current executions
	 * @return true if message was sent, false in case not
	 * @throws Exception 
	 */
	public boolean pushInfoPM(String hostName, String userName, Long[] executions) throws Exception{
		String serverIP=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_PM_PORT);
		UDPMessageStatePM message = new UDPMessageStatePM(serverIP, serverPort, hostName, userName, executions);		
		return sender.sendMessage(message);
	}
	
	/**
	 * Push info by UDP protocol to server port for executions reports
	 * @param hostName : current hostname
	 * @param executionCode : execution code in server
	 * @param state : last state for execution
	 * @param messageExecution : Short message with description about state
	 * @return Starts and configures an execution. This method must be used by other methods to configure, start and schedule an execution
	 * @throws Exception 
	 */
	public boolean pushInfoEXE(String hostName, long executionCode, ExecutionStateEnum state, String messageExecution) throws Exception{		
		String serverIP=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT);
		UDPMessageStateEXE message = new UDPMessageStateEXE(serverIP, serverPort, hostName, executionCode, state, messageExecution);
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
	public boolean pushInfoLogPM(String hostName, String component, String logMessage) throws Exception {
		String serverIP=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT);
		UDPMessageLogPM message = new UDPMessageLogPM(serverIP, serverPort, hostName, component, logMessage);
		return sender.sendMessage(message);
	}
	
}
