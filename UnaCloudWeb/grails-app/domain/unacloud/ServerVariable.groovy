package unacloud

import com.losandes.utils.UnaCloudConstants;

import unacloud.share.enums.ServerVariableTypeEnum;
import unacloud.pmallocators.AllocatorEnum;
import unacloud.share.enums.ServerVariableProgramEnum

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
	 * type of variable (String, Integer)
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
	 * Return values in case variable  is chosen based in a list
	 * @return
	 */
	def values(){
		if(this.name.equals(UnaCloudConstants.VM_DEFAULT_ALLOCATOR))
			return AllocatorEnum.getList()
		else []
	}
	
}
