package uniandes.unacloud.common.com.messages.udp;

import org.json.JSONObject;

import uniandes.unacloud.common.com.UDPMessageEnum;
import uniandes.unacloud.common.enums.VirtualMachineExecutionStateEnum;

/**
 * Class to represent an UDP Message Type State Virtual Machine
 * @author cdsbarrera
 *
 */
public class UDPMessageStateVM extends UnaCloudMessageUDP{

	
	/**
	 * Serial Version UID Serialize
	 */
	private static final long serialVersionUID = 3444791742727188007L;
	
	/**
	 * Tag ID of Execution
	 */
	public static final String TAG_VIRTUALMACHINE_CODE = "virtualMachineCode";
	
	/**
	 * Tag State of the Message
	 */
	public static final String TAG_STATE = "state";
	
	/**
	 * Tag Message of the Execution
	 */
	public static final String TAG_MESSAGE_EXECUTION = "message_execution";
	
	
	public UDPMessageStateVM(){
		
	}
	
	public UDPMessageStateVM(String ip, int port, String host, long virtualMachineCode, VirtualMachineExecutionStateEnum state, String messageExecution){
		super(ip, port, host, UDPMessageEnum.STATE_VM);
				
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TAG_VIRTUALMACHINE_CODE, virtualMachineCode);
		tempMessage.put(TAG_STATE, state.name());
		tempMessage.put(TAG_MESSAGE_EXECUTION, messageExecution==null?"None":messageExecution);
		this.setMessage(tempMessage);
		
	}
	
	public UDPMessageStateVM(UnaCloudMessageUDP message) {
		super(message.getIp(), message.getPort(), message.getHost(), message.getType());
		this.setMessage(message.getMessage());		
	}
	
	/**
	 * Return state of the Message
	 * @return VirtualMachineExecutionStateEnum State
	 */
	public VirtualMachineExecutionStateEnum getState() {
		return VirtualMachineExecutionStateEnum.getEnum(this.getMessage().getString(TAG_STATE));
	}
	
	/**
	 * Return Virtual Machine Code  in the message
	 * @return long Virtual Machine Code
	 */
	public Long getVirtualMachineCode() {
		try {
			return this.getMessage().getLong(TAG_VIRTUALMACHINE_CODE);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Return Message of the Execution realized.
	 * @return String Message
	 */
	public String getMessageExecution(){
		try{
			return this.getMessage().getString(TAG_MESSAGE_EXECUTION);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
