package communication;

import physicalmachine.Network;
import unacloudEnums.VirtualMachineExecutionStateEnum;

/**
 * Class used to send quickly messages to UnaCloud server
 * @author Clouder
 */
public class ServerMessageSender {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

    /**
     * Sends to UnaCloud server a message reporting the state of a virtual machine
     * @param virtualMachineCode The string that contains the id of the virtual machine to be reported
     * @param state The state of the reported virtual machine
     * @param message The state message of the reported virtual machine
     * @return If the message could be sent or not
     */
    public static boolean reportVirtualMachineState(long virtualMachineCode,VirtualMachineExecutionStateEnum state,String message){
    	return AbstractGrailsCommunicator.pushInfo("virtualMachineState/updateVirtualMachineState","hostname",Network.getHostname(),"executionId",virtualMachineCode,"state",state.toString(),"message",message);
    }
}
