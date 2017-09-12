package uniandes.unacloud.agent.net.send;

import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.net.tcp.TCPSender;
import uniandes.unacloud.common.net.tcp.message.ExecutionStateMessage;
import uniandes.unacloud.common.utils.UnaCloudConstants;

public class TCPCommunicator {
	
	/**
	 * Object to send tcp messages
	 */
	private TCPSender sender;
	
	/**
	 * Singleton instance
	 */
	private static TCPCommunicator instance;
	
	/**
	 * Responsible to return instance from this class
	 * @return singleton instance 
	 */
	public static TCPCommunicator getInstance() {
		if (instance == null)
			instance = new TCPCommunicator();
		return instance;
	}
	
	private TCPCommunicator() {
		sender = new TCPSender();
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
	public synchronized boolean pushInfoEXE(String hostName, long executionCode, ExecutionProcessEnum state, String messageExecution) throws Exception {		
		String serverIP = VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT);
		ExecutionStateMessage message = new ExecutionStateMessage(serverIP, serverPort, hostName, executionCode, state, messageExecution);
		sender.sendMessage(message);
		return true;
	}
}
