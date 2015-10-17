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
	List <PhysicalMachine> getOrderedImages(){
		return images.sort()		
	}	
	
}
