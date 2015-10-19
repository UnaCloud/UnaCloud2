package unacloud

import java.util.ArrayList;

import unacloud.enums.ClusterEnum;
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
	
	/**
	 * State of cluster
	 */
	ClusterEnum state = ClusterEnum.AVAILABLE;
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Returns cluster images sorted
	 * @return sorted images
	 */
	def List <PhysicalMachine> getOrderedImages(){
		return images.sort()		
	}	
	
	/**
	 * Returns if cluster is deployed or not
	 * @return a boolean 
	 */
	def isDeployed(){
		boolean isDeployed=false
		Long clusterId = this.id;
		def deployments= Deployment.where{status == DeploymentStateEnum.FINISHED && cluster.id==clusterId}.findAll()
		return deployments&&deployments.size()>0?true:false
	}
	
}
