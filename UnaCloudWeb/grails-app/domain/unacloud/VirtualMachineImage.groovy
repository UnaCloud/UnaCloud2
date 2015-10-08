package unacloud

import java.text.DecimalFormat
import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.VirtualMachineImageEnum;

class VirtualMachineImage {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Image name
	 */
	String name
	
	/**
	 * indicates if the image is public or not
	 */
    boolean isPublic
	
	/**
	 * Size of files
	 */
	long fixedDiskSize
	
	/**
	 * username used to access the image
	 */
	String user
	
	/**
	 * password used to access the image
	 */
	String password
	
	/**
	 * Image operating system 
	 */
	OperatingSystem operatingSystem
	
	/**
	 * access protocol (SSH, RDP)
	 */
	String accessProtocol
	
	/**
	 * Main file path (File that can be executed by hypervisor in order to
	 * deploy the machine)
	 */
	String mainFile
	
	/**
	 * Indicates how many times the image files had been edited
	 */
	int imageVersion
	
	/**
	 * token to validate image message send by client
	 */	
	String token
	
	/**
	 *Virtual machine state (DISABLE, AVAILABLE, COPYING) 
	 */
	VirtualMachineImageEnum state = VirtualMachineImageEnum.AVAILABLE;
	
	/**
	 * Owner 
	 */
	static belongsTo = [owner: User, repository: Repository]
	
	/**
	 * Last update date
	 */
	Date lastUpdate
	
	static constraints = {
    	mainFile (nullable: true)
		token nullable:true
		lastUpdate nullable:true
	}
	static mapping = {
		operatingSystem(lazy:false)
	}
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Indicates if the image is part of an active deployment
	 * @return a boolean indicating if the image is deployed
	 */
	def boolean isDeployed(){
		boolean isDeployed=false
		def deployments= Deployment.findByStatusNotEqual(DeploymentStateEnum.FINISHED)
		deployments.each (){
			it.cluster.images.each(){
				if(it.image==this)
					isDeployed=true
			}
		}
		return isDeployed
	}
	
	def String getSize(){
		DecimalFormat df = new DecimalFormat("#.00");
		long diskSize = fixedDiskSize/1024;
		if(diskSize>1){
			if(diskSize/1024>1){				
				diskSize = diskSize/1024
				if(diskSize/1024>1)
				return  df.format(diskSize/1024)+" GB"
				else return  df.format(diskSize)+" MB"
			}				
			else return  df.format(diskSize)+" KB"		
		}
		else return df.format(fixedDiskSize)+" Bytes"
	}
	
}	
