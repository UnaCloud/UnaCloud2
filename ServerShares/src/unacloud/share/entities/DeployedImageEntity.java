package unacloud.share.entities;

import java.util.List;

/**
 * Class to represent a Deployed image entity 
 * @author CesarF
 *
 */
public class DeployedImageEntity {
	
	private VirtualMachineImageEntity image;
	private List<VirtualMachineExecutionEntity> executions;
	
	public DeployedImageEntity(VirtualMachineImageEntity image,
			List<VirtualMachineExecutionEntity> executions) {
		this.image = image;
		this.executions = executions;
	}

	public DeployedImageEntity() {	
	}

	public VirtualMachineImageEntity getImage() {
		return image;
	}

	public void setImage(VirtualMachineImageEntity image) {
		this.image = image;
	}

	public List<VirtualMachineExecutionEntity> getExecutions() {
		return executions;
	}

	public void setExecutions(List<VirtualMachineExecutionEntity> executions) {
		this.executions = executions;
	}
}
