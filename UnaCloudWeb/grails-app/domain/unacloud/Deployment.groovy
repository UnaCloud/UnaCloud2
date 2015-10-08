package unacloud

//import back.services.ExternalCloudCallerService;
import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;

class Deployment {
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Deployed cluster representation 
	 */
	DeployedCluster cluster
	
	/**
	 * start time of the deployment
	 */
	Date startTime
	
	/**
	 * stop time of the deployment
	 */
	Date stopTime
	
	/**
	 * represent status of the deployment (ACTIVE of FINISHED)
	 */
	DeploymentStateEnum status
	
	/**
	 * Owner
	 */
	static belongsTo = [user: User]
	
	static constraints = {	
		stopTime nullable:true 
    }
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	
	
}
