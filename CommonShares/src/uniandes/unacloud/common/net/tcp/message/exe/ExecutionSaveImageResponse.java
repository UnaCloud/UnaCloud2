package uniandes.unacloud.common.net.tcp.message.exe;

import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionStartResponse.ExecutionState;

/**
 * Represents response to send current execution to server
 * @author CesarF
 *
 */
public class ExecutionSaveImageResponse extends UnaCloudResponse{

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
