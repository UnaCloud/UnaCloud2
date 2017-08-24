package uniandes.unacloud.common.net.tcp.message;

import org.json.JSONObject;

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
	
	/**
	 * Tag ID of Execution
	 */
	private static final String TAG_EXECUTION_CODE = "execution_code";
	
	/**
	 * Tag State of the Message
	 */
	private static final String TAG_STATE = "state";
	
	/**
	 * Tag Message of the Execution
	 */
	private static final String TAG_EXECUTION_MESSAGE = "message_execution";
	
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
	
	public ExecutionStateMessage(ExecutionStateMessage message) {
		this.setMessageByStringJson(message.getStringMessage());		
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
	public void setMessageByStringJson(String format) {
		super.setMessageByStringJson(format);
		JSONObject json;
		json = new JSONObject(format);		
		this.messageExecution = json.getString(TAG_EXECUTION_MESSAGE) == "None"? null : json.getString(TAG_EXECUTION_MESSAGE);
		this.executionCode = json.getLong(TAG_EXECUTION_CODE);
		this.state = json.getString(TAG_STATE);
	}
	
	@Override
	protected JSONObject getJsonMessage() {
		JSONObject obj = super.getJsonMessage();
		obj.put(TAG_EXECUTION_CODE, executionCode);
		obj.put(TAG_STATE, state);
		obj.put(TAG_EXECUTION_MESSAGE, messageExecution == null ? "None" : messageExecution);
		return obj;
	}
}
