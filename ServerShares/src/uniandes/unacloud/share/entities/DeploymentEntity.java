package uniandes.unacloud.share.entities;

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
	private Date stopTime;
	private DeploymentStateEnum state;
	private List<DeployedImageEntity> images;
	
	public DeploymentEntity(Long id, Date startTime, Date stopTime,
			DeploymentStateEnum state, List<DeployedImageEntity> images) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.stopTime = stopTime;
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

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
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
