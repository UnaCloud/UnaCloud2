package unacloud

import java.util.ArrayList;

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
	 * list of images, restrictions, clusters and deployments belonging to this user
	 */
	static hasMany = [images: VirtualMachineImage, restrictions: UserRestriction, userClusters: Cluster, deployments: Deployment]
	
	static constraints = {
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
		if(group)
			return group.users.find{it.id = this.id}?true:false;
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
		return this.userClusters.sort{it.name}
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
}
