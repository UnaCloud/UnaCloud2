package communication.messages.vmo;

import communication.UnaCloudAbstractResponse;
import communication.messages.vmo.VirtualMachineStartResponse.VirtualMachineState;

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
