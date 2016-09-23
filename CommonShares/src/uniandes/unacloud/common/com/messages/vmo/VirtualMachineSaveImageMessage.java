package uniandes.unacloud.common.com.messages.vmo;

import uniandes.unacloud.common.com.messages.VirtualMachineOperationMessage;

/**
 * Represents message to send current execution to server
 * @author CesarF
 *
 */
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