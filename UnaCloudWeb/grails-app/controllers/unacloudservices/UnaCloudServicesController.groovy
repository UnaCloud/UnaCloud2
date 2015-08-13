package unacloudservices

import back.services.AgentService;
import back.services.LogService;
import back.services.PhysicalMachineStateManagerService;
import back.services.VariableManagerService;

class UnaCloudServicesController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of physical machine state manager
	 */
	
	PhysicalMachineStateManagerService physicalMachineStateManagerService;
	
	/**
	 *  Representation of variable manager service
	 */
	
	VariableManagerService variableManagerService;
	
	/**
	 * Representation of agent service
	 */
	
	AgentService agentService;
	
	/**
	 * Representation of log service
	 */
	LogService logService;
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	
	/**
	 * Renders agent version variable
	 */
	
	def agentVersion(){
		render variableManagerService.getStringValue("AGENT_VERSION");
	}
	
	/**
	 * Changes server variables action. Redirects to configuration index when done
	 */
	
	def changeServerVariables(){
		variableManagerService.changeServerVariables(params)
		redirect(uri: "/configuration");
	}
	
	/**
	 * Changes agent version. Redirects to configuration index when done
	 * @return
	 */
	def updateAgentVersion(){
		variableManagerService.updateAgentVersion();
		redirect(uri: "/configuration");	
	}
	
	/**
	 * Agent files download service. Used by client updater  
	 * @return Agent files
	 */
	
	def agent(){
		response.setContentType("application/zip")
		response.setHeader("Content-disposition", "filename=agent.zip")
		agentService.copyAgentOnStream(response.outputStream,grailsAttributes.getApplicationContext().getResource("/").getFile())
		response.outputStream.flush()
	}
	
	/**
	 * Updater files download service. Used by admin users in order to set agent 
	 * files in a new physical machine
	 * @return Updater files
	 */
	
	def updater(){
		response.setContentType("application/zip")
		response.setHeader("Content-disposition", "filename=updater.zip")
		agentService.copyUpdaterOnStream(response.outputStream,grailsAttributes.getApplicationContext().getResource("/").getFile())
		response.outputStream.flush()
	}
	
	/**
	 * Creates a new log message
	 */
	def logMessage(){
		def component=params['component']
		def message=params['message']
		def hostname=params['hostname']
		logService.createLog("",component,message);
	}
}
