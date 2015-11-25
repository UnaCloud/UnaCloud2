package unacloud

import unacloud.enums.ServerVariableTypeEnum;

class ConfigurationController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of group services
	 */
	
	UserGroupService userGroupService
	
	/**
	 * Representation of configuration services
	 */
	ConfigurationService configurationService

	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------

	/**
	 * Makes session verifications before executing user administration actions
	 */
	
	def beforeInterceptor = {
		if(!session.user){
			flash.message="You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		else{
			if(!userGroupService.isAdmin(session.user)){
				flash.message="You must be administrator to see this content"
				redirect(uri:"/error", absolute:true)
				return false
			}
		}
	}
	
	/**
	 * Render page to list and edit variables
	 * @return
	 */
    def listVariables() { 
		[variables:ServerVariable.all]
	}
	
	/**
	 * Set value in a server variable
	 * @return
	 */
	def setVariable(){
		def variable = ServerVariable.get(params.id)
		if(variable){
			configurationService.setValue(variable, variable.serverVariableType.equals(ServerVariableTypeEnum.BOOLEAN)?
				params.value?'true':'false':params.value.equals("NoOne")?"":params.value)
			flash.message="The server variable has been modified"
			flash.type="success"
		}
		redirect(uri:"/config/variables", absolute:true)
	}
	
	/**
	 * Render page to manage agent configuration
	 * @return
	 */
	def agentConfig(){
		[agent:configurationService.getAgentVersion()]
	}
	
	/**
	 * Increases agent version
	 * @return
	 */
	def setAgentVersion(){
		configurationService.setAgentVersion();
		flash.message="The Agent version has been increased"
		flash.type="success"
		redirect(uri:"/config/agent", absolute:true)
	}
	
	def downloadAgent(){
		response.setContentType("application/zip")
		response.setHeader("Content-disposition", "filename=agent.zip")
		configurationService.copyUpdaterOnStream(response.outputStream,grailsAttributes.getApplicationContext().getResource("/").getFile())
		response.outputStream.flush()
	}
}
