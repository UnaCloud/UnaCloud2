package uniandes.unacloud.common.com.messages.vmo;

import uniandes.unacloud.common.com.UnaCloudAbstractResponse;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineStartResponse.VirtualMachineState;

/**
 * Represents response to send current execution to server
 * @author CesarF
 *
 */
public class VirtualMachineSaveImageResponse extends UnaCloudAbstractResponse{

	private static final long serialVersionUID = 4585003850300062718L;
	private VirtualMachineState state;
	private String message;
	public VirtualMachineState getState() {
		return state;
	}
	public void setState(VirtualMachineState state) {
		this.state = state;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	} 
	
	
}
