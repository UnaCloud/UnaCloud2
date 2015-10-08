package unacloud

import java.util.ArrayList;

import unacloud.enums.DeploymentStateEnum;

class Cluster {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Cluster name given by user
	 */
	String name
	
	/**
	 * List of virtual machine images in the cluster 
	 */
	static hasMany = [images: VirtualMachineImage]
	
	/**
	 * Owner
	 */
	static belongsTo = [user: User]
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Returns cluster images sorted
	 * @return sorted images
	 */
	List <PhysicalMachine> getOrderedImages(){
		return images.sort()		
	}
	
	/**
	 * Returns the cluster state 
	 * @return a boolean with the cluster current status
	 */
	def isDeployed(){
		boolean isDeployed=false
		def deployments= Deployment.findByStatusNotEqual(DeploymentStateEnum.FINISHED)
		deployments.each (){
			if(it.cluster.cluster==this)
				isDeployed=true
		}
		return isDeployed
	}	
	
}
