package communication.messages.vmo;

import communication.UnaCloudAbstractResponse;

public class VirtualMachineStartResponse extends UnaCloudAbstractResponse{
	private static final long serialVersionUID = -9042455333449782507L;
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
	public static enum VirtualMachineState{
		STARTING,NOT_COPY,FAILED,COPYNG,
	} 
	@Override
	public String toString() {
		return "VirtualMachineStartResponse [state=" + state + ", message=" + message + "]";
	}
}
