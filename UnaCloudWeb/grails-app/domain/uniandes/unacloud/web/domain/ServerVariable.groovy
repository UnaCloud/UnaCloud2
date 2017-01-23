package uniandes.unacloud.web.domain;

import uniandes.unacloud.common.utils.UnaCloudConstants;

import uniandes.unacloud.share.enums.ServerVariableTypeEnum;
import uniandes.unacloud.web.pmallocators.AllocatorEnum;
import uniandes.unacloud.share.enums.ServerVariableProgramEnum

/**
 * Entity to represent a variable that is used by server to connect with services talk to other applications and generate configuration files for agents
 * @author CesarF
 *
 */
class ServerVariable {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Server variable name
	 */
	String name
	
	/**
	 * Server variable data
	 */
	String variable
	
	/**
	 * type of variable (String, Integer, Boolean)
	 */
	ServerVariableTypeEnum serverVariableType
	
	/**
	 * Program that used variable 
	 */	
	ServerVariableProgramEnum program;
	
	/**
	 * Return if a variable is a list separated by comma
	 */
	boolean isList = false
	
	/**
	 * Used to manage which variables could be used for agent configuration
	 */
	boolean serverOnly = true
	
    static constraints = {
    }
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	
	/**
	 * Returns values in variables which are list, currently only used for DEFAULT ALLOCATOR
	 * @return list of values
	 */
	def values(){
		if(this.name.equals(UnaCloudConstants.VM_DEFAULT_ALLOCATOR))
			return AllocatorEnum.getList()
		else []
	}
	
}
