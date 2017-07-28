package uniandes.unacloud.common.net.tcp.message;

import java.io.Serializable;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;

/**
 * Representation of response from client
 * @author CesarF
 *
 */
public class UnaCloudResponse implements Serializable {

	private static final long serialVersionUID = 3900739048594542173L;

	private ExecutionProcessEnum state;
	
	private String message;
	
	public UnaCloudResponse() {
		
	}
	
	public UnaCloudResponse(String message) {
		this.message = message;		
	}
	
	public UnaCloudResponse(ExecutionProcessEnum state) {
		this.state = state;	
	}
	
	public UnaCloudResponse(String message, ExecutionProcessEnum state) {
		this.state = state;	
		this.message = message;		
	}
	
	public String getMessage() {
		return message;
	}
	
	public ExecutionProcessEnum getState() {
		return state;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setState(ExecutionProcessEnum state) {
		this.state = state;
	}
}
