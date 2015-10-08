package unacloud

import java.util.ArrayList;

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
	
	def getOrderedImages(){
		if(!this.images){
			this.images = []
			this.save()
		}
		return this.images.sort{it.name}
	}
}
