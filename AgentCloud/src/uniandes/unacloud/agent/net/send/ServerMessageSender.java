package uniandes.unacloud.agent.net.send;

import uniandes.unacloud.agent.host.system.OSFactory;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;


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
    public static boolean reportExecutionState(long executionCode, ExecutionProcessEnum state, String message) throws Exception {    	
    	return TCPCommunicator.getInstance().pushInfoEXE(OSFactory.getOS().getHostname(), executionCode, state, message);
    }
    
    /**
     * Send message reporting the state of a physical machine
     * @param executions
     * @throws Exception
     */
	public static void reportPhyisicalMachine(Long[] executions) throws Exception {
		UDPCommunicator.getInstance().pushInfoPM(OSFactory.getOS().getHostname(), OSFactory.getOS().getUserName(), executions);
	}
	
	/**
	 * Send message to be save in log database
	 * @param component where event happened
	 * @param message describes error
	 * @throws Exception
	 */
	public static void reportMachineLogEvent(String component, String message) throws Exception {
		UDPCommunicator.getInstance().pushInfoLogPM(OSFactory.getOS().getHostname(), component, message);
	}
	
	/**
     * Sends message reporting the state of a physical machine with extra information
	 * @param freeSpace current free space in bytes in physical machine
	 * @param dataSpace total space in bytes in physical machine
	 * @param version current agent version
	 * @throws Exception
	 */
	public static void reportPhyisicalMachine(long freeSpace, long dataSpace, String version) throws Exception{
		UDPCommunicator.getInstance().pushInfoPM(OSFactory.getOS().getHostname(), OSFactory.getOS().getUserName(), freeSpace, dataSpace, version);
	}
}
