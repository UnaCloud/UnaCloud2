package unacloudweb

import uniandes.unacloud.web.services.init.ControlService;

/**
 * This Job is responsible to update status of all current active executions
 * This job is execute each minute after first delay (60 seconds)
 * @author CesarF
 *
 */
class ExecutionControlJob {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of control services
	 */	
	ControlService controlService
	
	
	def group = "control_group"
	def description = "Job to control status of active executions"
	
    static triggers = {
		simple name: 'executionControl', startDelay: 60000, repeatInterval: 60000
    }

    def execute() {
        controlService.validateExecutionStates()
    }
}
