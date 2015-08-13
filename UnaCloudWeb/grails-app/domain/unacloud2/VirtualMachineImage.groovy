package unacloud2

import unacloud2.enums.DeploymentStateEnum;

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
	 * Disk size configured in image files in GB
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
	
	String externalId
	
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
	
	static constraints = {
    	mainFile (nullable: true)
		token nullable:true
		externalId (nullable: true)
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
	def isDeployed(){
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
	
}	
