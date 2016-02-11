package unacloud

import java.util.ArrayList;

import unacloud.enums.ClusterEnum;
import unacloud.share.enums.DeploymentStateEnum;
import unacloud.share.enums.VirtualMachineImageEnum;

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
		def deployments= Deployment.where{status != DeploymentStateEnum.FINISHED && cluster.id==clusterId}.findAll()
		return deployments&&deployments.size()>0?true:false
	}
	
	/**
	 * Validates if cluster is FREEZE. If all images are available changes status to AVAILABLE
	 * @return
	 */
	def update(){
		if(state.equals(ClusterEnum.FREEZE)
			&&images.findAll{it.state!=VirtualMachineImageEnum.AVAILABLE}.size()==0){
			state=ClusterEnum.AVAILABLE;
			this.save(failOnError:true)
		}
	}
	
}
