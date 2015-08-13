package unacloudws.responses;

import java.util.List;

public class DeployedImageWS {
	
	int id;
	ImageWS imageWS;
	List<VirtualMachineExecutionWS> virtualMachines;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ImageWS getImageWS() {
		return imageWS;
	}
	public void setImageWS(ImageWS imageWS) {
		this.imageWS = imageWS;
	}
	public List<VirtualMachineExecutionWS> getVirtualMachines() {
		return virtualMachines;
	}
	public void setVirtualMachines(List<VirtualMachineExecutionWS> virtualMachines) {
		this.virtualMachines = virtualMachines;
	}
	
}
