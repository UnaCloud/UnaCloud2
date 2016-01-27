package unacloud

import java.util.ArrayList;

import com.losandes.utils.Constants;

import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.UserRestrictionEnum;
import unacloud.enums.UserStateEnum;
import unacloud.enums.VirtualMachineImageEnum;

class User {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * User name and lastname
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
	static hasMany = [images: VirtualMachineImage, restrictions: UserRestriction, userClusters: Cluster, deployments: Deployment]
	
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
	 * 
	 * @return true is user is admin, false is not
	 */
	
	def boolean isAdmin(){
		UserGroup group = UserGroup.findByName(Constants.ADMIN_GROUP);
		if(group)return group.users.find{it.id == this.id}?true:false;
		return false
	}
	/**
	 * Return the list of images owned by user sorted by name
	 * @return
	 */
	def getOrderedImages(){
		if(!this.images){
			this.images = []
			this.save()
		}
		return this.images.sort{it.name}
	}
	/**
	 * Return the list of clusters owned by user sorted by name
	 * @return
	 */
	def getOrderedClusters(){
		if(!this.userClusters){
			this.userClusters = []
			this.save()
		}
		def clusters = this.userClusters.sort{it.name}
		for(Cluster cl in clusters)cl.update()
		return clusters
	}
	
	/**
	 * Return the list of images owned by user sorted by name and state AVAILABLE
	 * @return
	 */
	def getAvailableImages(){
		if(!this.images){
			this.images = []
			this.save()
		}
		return this.images.findAll{it.state==VirtualMachineImageEnum.AVAILABLE}.sort{it.name}
	}
	
	/**
	 * Return a restriction of user, null if does not exist
	 */
	def getRestriction(UserRestrictionEnum restriction){
		this.restrictions.find{it.name==restriction.toString()}
	}
	
	/**
	 * Return all groups where exists the restriction, null if does not exist
	 */
	def getGroupsWithRestriction(UserRestrictionEnum restriction){ 
		return UserGroup.where {users{id==this.id} && restrictions{name==restriction.toString()}}.findAll()
	}
	
	/**
	 * return the active deployments belonging to the user
	 * @return active deployments related to user
	 */
	def getActiveDeployments(){
		List activeDeployments= new ArrayList()
		for (deployment in deployments){
			if(deployment.isActive()) activeDeployments.add(deployment)
			else deployment.putAt('status', DeploymentStateEnum.FINISHED)
		}
		return activeDeployments
	}
	
	/**
	 * Method used to return databaseId
	 * @return
	 */
	def Long getDatabaseId(){
		return id;
	}
}
