package uniandes.unacloud.agent.communication.send;

import uniandes.unacloud.agent.system.OSFactory;
import uniandes.unacloud.common.enums.ExecutionStateEnum;


/**
 * Class used to send  messages to UnaCloud server
 * @author Clouder
 */
public class ServerMessageSender {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

    /**
     * Sends to UnaCloud control server a message reporting the state of one execution
     * @param executionCode The string that contains the id of the execution to be reported
     * @param state The state of the reported execution
     * @param message The state message of the reported execution
     * @return If the message could be sent or not
     * @throws Exception 
     */
    public static boolean reportExecutionState(long executionCode,ExecutionStateEnum state,String message) throws Exception{    	
    	return UDPCommunicator.getInstance().pushInfoEXE(OSFactory.getOS().getHostname(), executionCode, state, message);
    }
    
    /**
     * Send message reporting the state of a physical machine
     * @param executions
     * @throws Exception
     */
	public static void reportPhyisicalMachine(Long[] executions) throws Exception{
		UDPCommunicator.getInstance().pushInfoPM(OSFactory.getOS().getHostname(), OSFactory.getOS().getUserName(), executions);
	}
	
	/**
	 * Send message to be save in log database
	 * @param component where event happened
	 * @param message describes error
	 * @throws Exception
	 */
	public static void reportMachineLogEvent(String component, String message) throws Exception{
		UDPCommunicator.getInstance().pushInfoLogPM(OSFactory.getOS().getHostname(), component, message);
	}
}
