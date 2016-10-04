package uniandes.unacloud.common.com.messages.udp;

import org.json.JSONObject;

import uniandes.unacloud.common.com.UDPMessageEnum;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

/**
 * Class to represent an UDP Message Type State Execution
 * @author cdsbarrera
 *
 */
public class UDPMessageStateEXE extends UnaCloudMessageUDP{

	
	/**
	 * Serial Version UID Serialize
	 */
	private static final long serialVersionUID = 3444791742727188007L;
	
	/**
	 * Tag ID of Execution
	 */
	public static final String TAG_EXECUTION_CODE = "execution_code";
	
	/**
	 * Tag State of the Message
	 */
	public static final String TAG_STATE = "state";
	
	/**
	 * Tag Message of the Execution
	 */
	public static final String TAG_EXECUTION_MESSAGE = "message_execution";
	
	
	public UDPMessageStateEXE(){
		
	}
	
	public UDPMessageStateEXE(String ip, int port, String host, long executionCode, ExecutionStateEnum state, String messageExecution){
		super(ip, port, host, UDPMessageEnum.STATE_EXE);
				
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(TAG_EXECUTION_CODE, executionCode);
		tempMessage.put(TAG_STATE, state.name());
		tempMessage.put(TAG_EXECUTION_MESSAGE, messageExecution==null?"None":messageExecution);
		this.setMessage(tempMessage);
		
	}
	
	public UDPMessageStateEXE(UnaCloudMessageUDP message) {
		super(message.getIp(), message.getPort(), message.getHost(), message.getType());
		this.setMessage(message.getMessage());		
	}
	
	/**
	 * Returns state of the Message
	 * @return ExecutionStateEnum State
	 */
	public ExecutionStateEnum getState() {
		return ExecutionStateEnum.getEnum(this.getMessage().getString(TAG_STATE));
	}
	
	/**
	 * Returns Execution Code in message
	 * @return long Execution Code
	 */
	public Long getExecutionCode() {
		try {
			return this.getMessage().getLong(TAG_EXECUTION_CODE);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns Message of the Execution realized.
	 * @return String Message
	 */
	public String getExecutionMessage(){
		try{
			return this.getMessage().getString(TAG_EXECUTION_MESSAGE);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
