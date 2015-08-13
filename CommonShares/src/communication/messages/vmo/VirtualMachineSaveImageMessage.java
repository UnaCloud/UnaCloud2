package communication.messages.vmo;

import communication.messages.VirtualMachineOperationMessage;

public class VirtualMachineSaveImageMessage extends VirtualMachineOperationMessage{

	private long imageId;
	private String tokenCom;
	
	private static final long serialVersionUID = 3147489071041260127L;
	
	public VirtualMachineSaveImageMessage() {
		super(VM_SAVE_IMG);
	}

	public long getImageId() {
		return imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
	}
	public String getTokenCom() {
		return tokenCom;
	}

	public void setTokenCom(String tokenCom) {
		this.tokenCom = tokenCom;
	}

}