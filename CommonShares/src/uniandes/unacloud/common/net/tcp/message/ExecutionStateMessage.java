package uniandes.unacloud.common.net.tcp.message;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.net.udp.message.UDPMessageEnum;

/**
 * Class to represent an UDP Message Type State Execution
 * @author cdsbarrera, CesarF
 *
 */
public class ExecutionStateMessage extends UnaCloudMessage {

	
	/**
	 * Serial Version UID Serialize
	 */
	private static final long serialVersionUID = 3444791742727188007L;
	
	private long executionCode;
	
	private String state;
	
	private String messageExecution;
	
	public ExecutionStateMessage() {
		
	}
	
	public ExecutionStateMessage(String ip, int port, String host, long executionCode, ExecutionProcessEnum state, String messageExecution) {
		super(ip, port, host, UDPMessageEnum.STATE_EXE.name());			
		this.executionCode = executionCode;
		this.state = state.name();
		this.messageExecution = messageExecution;		
	}
		
	/**
	 * Returns state of the Message
	 * @return ExecutionStateEnum State
	 */
	public ExecutionProcessEnum getState() {
		return ExecutionProcessEnum.getEnum(state);
	}
	
	/**
	 * Returns Execution Code in message
	 * @return long Execution Code
	 */
	public Long getExecutionCode() {
		return executionCode;
	}
	
	/**
	 * Returns Message of the Execution realized.
	 * @return String Message
	 */
	public String getExecutionMessage() {
		return messageExecution;
	}

	@Override
	public String toString() {
		return "ExecutionStateMessage [executionCode=" + executionCode
				+ ", state=" + state + ", messageExecution=" + messageExecution
				+ "] " + super.toString();
	}
	
	
}
