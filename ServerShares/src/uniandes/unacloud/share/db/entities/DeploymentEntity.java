package uniandes.unacloud.share.db.entities;

import java.util.Date;
import java.util.List;

import uniandes.unacloud.share.enums.DeploymentStateEnum;


/**
 * Class to represent a Deployment entity 
 * @author CesarF
 *
 */
public class DeploymentEntity {
	
	private Long id;
	
	private Date startTime;
	
	
	private DeploymentStateEnum state;
	
	private List<DeployedImageEntity> images;
	
	public DeploymentEntity(Long id, Date startTime,
			DeploymentStateEnum state, List<DeployedImageEntity> images) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.state = state;
		this.images = images;
	}

	public DeploymentEntity() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public DeploymentStateEnum getState() {
		return state;
	}

	public void setState(DeploymentStateEnum state) {
		this.state = state;
	}

	public List<DeployedImageEntity> getImages() {
		return images;
	}

	public void setImages(List<DeployedImageEntity> images) {
		this.images = images;
	}
	
}
