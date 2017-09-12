package uniandes.unacloud.share.db.entities;

import java.util.List;

/**
 * Class to represent a Deployed image entity 
 * @author CesarF
 *
 */
public class DeployedImageEntity {
	
	private ImageEntity image;
	
	private List<ExecutionEntity> executions;
	
	public DeployedImageEntity(ImageEntity image,
			List<ExecutionEntity> executions) {
		this.image = image;
		this.executions = executions;
	}

	public DeployedImageEntity() {	
	}

	public ImageEntity getImage() {
		return image;
	}

	public void setImage(ImageEntity image) {
		this.image = image;
	}

	public List<ExecutionEntity> getExecutions() {
		return executions;
	}

	public void setExecutions(List<ExecutionEntity> executions) {
		this.executions = executions;
	}
}
