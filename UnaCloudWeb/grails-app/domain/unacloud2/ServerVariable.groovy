package unacloud2

import unacloud2.enums.ServerVariableTypeEnum;

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
}
