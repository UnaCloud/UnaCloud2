package uniandes.unacloud.web.domain

import java.util.ArrayList;

import uniandes.unacloud.common.utils.UnaCloudConstants;

import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.UserRestrictionEnum;
import uniandes.unacloud.share.enums.UserStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Entity to represent a User with access to UnaCloud.
 * @author CesarF
 *
 */
class User {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * user Fullname 
	 */
	String name
	
	/**
	 * user unique username 
	 */
	String username
	
	/**
	 * user access password
	 */
	String password
	
	/**
	 * Key required to use API services
	 */
	String apiKey
	
	/**
	 * Register Date
	 */
	Date registerDate
	
	/**
	 * User description
	 */
	String description
	
	/**
	 * User email
	 */
	String email
	
	/**
	 * list of images, restrictions, clusters and deployments belonging to this user
	 */
	static hasMany = [images: Image, restrictions: UserRestriction, userClusters: Cluster, deployments: Deployment]
	
	/**
	 * State of user
	 */
	UserStateEnum status = UserStateEnum.AVAILABLE;
	
	static constraints = {
		username unique: true
		email nullable:true
    }
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Validates if user is administrator
	 * @return true is user is admin, false is not
	 */
	
	def boolean isAdmin() {
		UserGroup group = UserGroup.findByName(UnaCloudConstants.ADMIN_GROUP);
		if (group)
			return group.users.find{it.id == this.id} ? true : false;
		return false
	}
	
	/**
	 * Returns the list of images owned by user sorted by name
	 * @return list of sorted images
	 */
	def getOrderedImages() {
		if (!this.images) {
			this.images = []
			this.save()
		}
		return this.images.sort{it.name}
	}

	/**
	 * Returns the list of unavailable images in this user: state = UNAVAILABLE
	 * @return
	 */
	def getUnavailableImages() {
		if (!this.images) {
			this.images = []
			this.save()
		}
		return this.images.findAll{it.state == ImageEnum.UNAVAILABLE};
	}
	
	/**
	 * Returns the list of clusters owned by user sorted by name
	 * @return list of sorted cluster
	 */
	def getOrderedClusters() {
		if (!this.userClusters) {
			this.userClusters = []
			this.save()
		}
		def clusters = this.userClusters.sort{it.name}
		for (Cluster cl in clusters)
			cl.update()
		return clusters
	}
	
	/**
	 * Returns the list of images owned by user sorted by name and state AVAILABLE
	 * @return list of available images
	 */
	def getAvailableImages() {
		if (!this.images) {
			this.images = []
			this.save() 
		} 
		return this.images.findAll{it.state == ImageEnum.AVAILABLE}.sort{it.name}
	}
	
	/**
	 * Returns the list of images owned by user sorted by name and state different to AVAILABLE
	 * @return list of not available images
	 */
	def getNotAvailableImages() {
		if(!this.images){
			this.images = []
			this.save()
		}
		return this.images.findAll{it.state != ImageEnum.AVAILABLE}.sort{it.name}
	}
	
	/**
	 * Searches and return a restriction in user restriction list
	 * @param user restriction to be requested
	 * @return a requested restriction of user, null if does not exist
	 */
	def getRestriction(UserRestrictionEnum restriction) {
		this.restrictions.find{it.name == restriction.toString()}
	}
	
	/**
	 * Searches and return a restriction in user group list
	 * @param user restriction to be requested
	 * @return all groups where exists the restriction, null if does not exist
	 */
	def getGroupsWithRestriction(UserRestrictionEnum restriction) { 
		return UserGroup.where {users{id == this.id} && restrictions{name == restriction.toString()}}.findAll()
	}
	
	/**
	 * List active deployments which belong to user
	 * @return active deployments related to user
	 */
	def getActiveDeployments() {
		List activeDeployments= new ArrayList()
		for (deployment in deployments) {
			if (deployment.isActive()) 
				activeDeployments.add(deployment)
			else 
				deployment.putAt('status', DeploymentStateEnum.FINISHED)
		}
		return activeDeployments
	}
	
	/**
	 * Returns database id
	 * @return Long id
	 */
	def Long getDatabaseId() {
		return id;
	}
	
	/**
	 * Disables User and delete all components that user is owner
	 */
	def deprecate() {
		this.putAt("status", UserStateEnum.DISABLE);
		for (UserRestriction restriction in restrictions)	
			restriction.delete()		
		restrictions = []
		for (Deployment deploy in deployments)
			deploy.deleteDeploy()
		deployments = []
		for (Image image in images)
			image.putAt("state", ImageEnum.IN_QUEUE)		
		this.save()
	}
	
	/**
	 * Validates if currently user has an image with the same name.
	 * This avoids to override files in same folder.
	 * @param name of image
	 * @return true in case image exist in user false in case not
	 */
	def existImage(String name) {
		return images.find{it.name == name} != null
	}
}
