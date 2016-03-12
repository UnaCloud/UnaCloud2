package unacloud;

import unacloud.share.enums.ServerVariableTypeEnum;

/**
 * This Controller contains actions to manage configuration services: shows and updates server variables, download and update agent.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions
 * only administrator users can call this actions.
 * @author CesarF
 *
 */
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
			def user = User.get(session.user.id)
			session.user.refresh(user)
			if(!userGroupService.isAdmin(user)){
				flash.message="You must be administrator to see this content"
				redirect(uri:"/error", absolute:true)
				return false
			}
		}
	}
	
	/**
	 * Renders page to list and edit variables
	 */
    def listVariables() { 
		[variables:ServerVariable.all]
	}
	
	/**
	 * Sets value in a server variable
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
	 * Renders page to manage agent configuration
	 */
	def agentConfig(){
		[agent:configurationService.getAgentVersion()]
	}
	
	/**
	 * Increases agent version
	 */
	def setAgentVersion(){
		configurationService.setAgentVersion();
		flash.message="The Agent version has been increased"
		flash.type="success"
		redirect(uri:"/config/agent", absolute:true)
	}
	
	/**
	 * call service to load files in stream
	 */
	def downloadAgent(){
		response.setContentType("application/zip")
		response.setHeader("Content-disposition", "filename=agent.zip")
		configurationService.copyUpdaterOnStream(response.outputStream,grailsAttributes.getApplicationContext().getResource("/").getFile())
		response.outputStream.flush()
	}
}
