package uniandes.unacloud.web.domain

import uniandes.unacloud.common.utils.CalendarUtils;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Entity to represent a group of instances requested by user to be executed on UnaCloud infrastructure.
 * Deployment has a cluster, a list of image in cluster, a time range and a status
 * @author CesarF
 *
 */
class Deployment {
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Deployed cluster representation 
	 */
	Cluster cluster
	
	/**
	 * start time of the deployment
	 */
	Date startTime
	
	/**
	 * stop time of the deployment
	 */
	Date stopTime
	
	/**
	 * represent status of the deployment (ACTIVE, FINISHED)
	 */
	DeploymentStateEnum status
	
	/**
	 * list of deployed images present in the deployment
	 */
	static hasMany = [images: DeployedImage]
	
	/**
	 * Owner
	 */
	static belongsTo = [user: User]
	
	/**
	 * Stop time is not defined possibly
	 * cluster could be deleted but deployment history not
	 */
	static constraints = {	
		stopTime nullable:true 
		cluster nullable:true
    }
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
		
	/**
	 * Verifies and refresh the deployment status
	 * @return After refreshing update process return true if the deployment is active or false in case of not.
	 */
	def isActive() {
		if (status == DeploymentStateEnum.ACTIVE) {
			for (image in images) {
				if(image.getActiveExecutions().size() > 0)				
					return true
			}
		}
		return false
	}
	
	/**
	 * Returns database id for entity
	 * @return Long id
	 */
	def Long getDatabaseId() {
		return id;
	}
	
	/**
	 * Deletes all history for deployment and images.
	 * It is necessary this method due to belong property among classes
	 */
	def deleteDeploy() {
		for (DeployedImage image: images) {
			image.executions.each {
				def exec = it
				ExecutionHistory.where{execution == exec}.list().each {
					it.delete();
				}
				exec.delete();
			}
			image.delete()
		}
		this.delete()
	}
}
