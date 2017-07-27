package uniandes.unacloud.common.net.tcp.message.exe;

import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;

/**
 * Represents response from agent to start an execution
 * @author CesarF
 *
 */
public class ExecutionStartResponse extends UnaCloudResponse {
	
	private static final long serialVersionUID = -9042455333449782507L;
	
	private ExecutionState state;
	
	private String message;
	
	public ExecutionState getState() {
		return state;
	}
	
	public void setState(ExecutionState state) {
		this.state = state;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static enum ExecutionState {
		STARTING, NOT_COPY, FAILED, COPYNG,
	}
	
	@Override
	public String toString() {
		return "ExecutionStartResponse [state=" + state + ", message=" + message + "]";
	}
}
