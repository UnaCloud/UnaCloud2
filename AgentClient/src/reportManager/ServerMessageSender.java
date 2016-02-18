package reportManager;

import com.andes.enums.VirtualMachineExecutionStateEnum;
import com.losandes.utils.OperatingSystem;

import communication.UDPMessageEnum;
import communication.send.UDPCommunicator;


/**
 * Class used to send quickly messages to UnaCloud server
 * @author Clouder
 */
public class ServerMessageSender {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

    /**
     * Sends to UnaCloud control server a message reporting the state of a virtual machine
     * @param virtualMachineCode The string that contains the id of the virtual machine to be reported
     * @param state The state of the reported virtual machine
     * @param message The state message of the reported virtual machine
     * @return If the message could be sent or not
     */
    public static boolean reportVirtualMachineState(long virtualMachineCode,VirtualMachineExecutionStateEnum state,String message){    	
    	return UDPCommunicator.getInstance().pushInfo(UDPMessageEnum.STATE_VM, "hostname",OperatingSystem.getHostname(),"executionId",virtualMachineCode,"state",state.toString(),"message",message);
    }
    
    /**
	 * reports a log in event
	 */
	public static void reportPhyisicalMachine(String executions){
		if(executions!=null)UDPCommunicator.getInstance().pushInfo(UDPMessageEnum.STATE_PM, "hostname",OperatingSystem.getHostname(),"hostuser",OperatingSystem.getUserName(),"executions",executions);
		else UDPCommunicator.getInstance().pushInfo(UDPMessageEnum.STATE_PM, "hostname",OperatingSystem.getHostname(),"hostuser",OperatingSystem.getUserName());
	}
	/**
	 * 
	 * @param error
	 */
	public static void reportMachineLogEvent(String component, String message){
		UDPCommunicator.getInstance().pushInfo(UDPMessageEnum.LOG_PM, "hostname",OperatingSystem.getHostname(),"component",component, "message",message);
	}
}
