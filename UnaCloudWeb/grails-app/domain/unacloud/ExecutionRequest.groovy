package unacloud

import java.util.Date;

import unacloud.share.enums.VirtualMachineExecutionStateEnum;

class ExecutionRequest {
	
	/**
	 * Node state (REQUESTED,COPYING,CONFIGURING,DEPLOYING,DEPLOYED,FAILED,FINISHED)
	 */
	VirtualMachineExecutionStateEnum status
	
	/**
	 * Date when the node was stopped
	 */
	Date requestTime
	
	/**
	 * Execution
	 */
	static belongsTo=  [execution:VirtualMachineExecution]

    static constraints = {
		status nullable:false
		requestTime nullable: false
    }
}
