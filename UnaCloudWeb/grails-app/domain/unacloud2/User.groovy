package unacloud2

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
	 * User role (Administrator or normal user)
	 */
	String userType
	
	/**
	 * user access password
	 */
	String password
	
	/**
	 * Key required to use API services
	 */
	String apiKey
	
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
	 * return the list of images sorted by name
	 * @return ordered list of images
	 */
	ArrayList <VirtualMachineImage> getOrderedImages(){
		VirtualMachineImageComparator c= new VirtualMachineImageComparator()
		ArrayList <VirtualMachineImage> array = new ArrayList(images).sort(true,c)
		return array
	}
	
	/**
	 * return the list of clusters sorted by name
	 * @return ordered list of clusters
	 */
	ArrayList <Cluster> getOrderedClusters(){
		ClusterComparator c= new ClusterComparator()
		ArrayList <Cluster> array = new ArrayList(userClusters).sort(true,c)
		return array
	}
	
	/**
	 * return the active deployments belonging to the user
	 * @return active deployments related to user
	 */
	def getActiveDeployments(){
		ArrayList activeDeployments= new ArrayList()
		for (deployment in deployments){
			if(deployment.isActive()) activeDeployments.add(deployment)
			else deployment.status='FINISHED'
		}
		return activeDeployments
	}
	
	
	
}
