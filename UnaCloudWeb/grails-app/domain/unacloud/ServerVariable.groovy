package unacloud

import unacloud.enums.ServerVariableTypeEnum;
import unacloud.pmallocators.AllocatorEnum;

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
	 * server side variable only
	 */
	boolean serverOnly
	
    static constraints = {
    }
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Return true if variable is chosen based in a list, false is not
	 * @return true or false
	 */
	def isList(){
		if(this.name in ['VM_DEFAULT_ALLOCATOR'])return true
		return false
	}
	
	/**
	 * Return values in case variable  is chosen based in a list
	 * @return
	 */
	def values(){
		if(this.name.equals('VM_DEFAULT_ALLOCATOR'))
			return AllocatorEnum.getList()
		else []
	}
	
}
