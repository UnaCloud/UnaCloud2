package unacloud.entities;

import java.util.Date;
import java.util.List;

import unacloud.enums.DeploymentStateEnum;

/**
 * Class to represent an entity from domain 
 * Represents Deployment
 * @author Cesar
 *
 */
public class Deployment {
	
	private Long id;
	private Date startTime;
	private Date stopTime;
	private DeploymentStateEnum state;
	private List<DeployedImage> images;
	
	public Deployment(Long id, Date startTime, Date stopTime,
			DeploymentStateEnum state, List<DeployedImage> images) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.state = state;
		this.images = images;
	}

	public Deployment() {
		
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

	public List<DeployedImage> getImages() {
		return images;
	}

	public void setImages(List<DeployedImage> images) {
		this.images = images;
	}
	
}
