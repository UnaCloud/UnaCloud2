package uniandes.unacloud.web.domain

import java.util.ArrayList;

import uniandes.unacloud.web.domain.enums.ClusterEnum;
import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Entity to represent a cluster; a group of images configured to be deployed together.
 * @author CesarF
 *
 */
class Cluster {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Cluster name given by user
	 */
	String name
	
	/**
	 * List of images in the cluster 
	 */
	static hasMany = [images: Image]
	
	/**
	 * Owner
	 */
	static belongsTo = [user: User]
	
	/**
	 * State of cluster
	 * it could be UNAVAILABLE, DISABLE, AVAILABLE, FREEZE
	 */
	ClusterEnum state = ClusterEnum.AVAILABLE;
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Returns cluster images sorted
	 * @return sorted images
	 */
	def List <PhysicalMachine> getOrderedImages() {
		return images.sort()		
	}	
	
	/**
	 * Returns if cluster is deployed or not
	 * @return a boolean 
	 */
	def isDeployed() {
		boolean isDeployed=false
		Long clusterId = this.id;
		def deployments = Deployment.where{status != DeploymentStateEnum.FINISHED && cluster.id == clusterId}.findAll()
		return deployments && deployments.size() > 0 ? true : false
	}
	
	/**
	 * Validates if cluster is FREEZE. 
	 * If all images are available changes status to AVAILABLE
	 */
	def update() {
		if (state.equals(ClusterEnum.FREEZE)
			&& images.findAll{it.state != ImageEnum.AVAILABLE}.size() == 0) {
			state = ClusterEnum.AVAILABLE;
			this.save(failOnError:true)
		}
	}
	
}
