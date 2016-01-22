package unacloud.entities;

import java.util.List;

/**
 * Class to represent an entity from domain 
 * Represents Deployment
 * @author Cesar
 *
 */
public class DeployedImage {
	
	private VirtualMachineImage image;
	private List<VirtualMachineExecution> executions;
	
	public DeployedImage(VirtualMachineImage image,
			List<VirtualMachineExecution> executions) {
		this.image = image;
		this.executions = executions;
	}

	public DeployedImage() {	
	}

	public VirtualMachineImage getImage() {
		return image;
	}

	public void setImage(VirtualMachineImage image) {
		this.image = image;
	}

	public List<VirtualMachineExecution> getExecutions() {
		return executions;
	}

	public void setExecutions(List<VirtualMachineExecution> executions) {
		this.executions = executions;
	}
}
