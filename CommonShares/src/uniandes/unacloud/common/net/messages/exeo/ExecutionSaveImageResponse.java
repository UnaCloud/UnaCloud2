package uniandes.unacloud.common.net.messages.exeo;

import uniandes.unacloud.common.net.messages.UnaCloudAbstractResponse;
import uniandes.unacloud.common.net.messages.exeo.ExecutionStartResponse.ExecutionState;

/**
 * Represents response to send current execution to server
 * @author CesarF
 *
 */
public class ExecutionSaveImageResponse extends UnaCloudAbstractResponse{

	private static final long serialVersionUID = 4585003850300062718L;
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
	
	
}
