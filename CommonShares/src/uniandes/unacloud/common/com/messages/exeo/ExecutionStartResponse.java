package uniandes.unacloud.common.com.messages.exeo;

import uniandes.unacloud.common.com.UnaCloudAbstractResponse;

/**
 * Represents response from agent to start an execution
 * @author CesarF
 *
 */
public class ExecutionStartResponse extends UnaCloudAbstractResponse{
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
	public static enum ExecutionState{
		STARTING,NOT_COPY,FAILED,COPYNG,
	} 
	@Override
	public String toString() {
		return "ExecutionStartResponse [state=" + state + ", message=" + message + "]";
	}
}
