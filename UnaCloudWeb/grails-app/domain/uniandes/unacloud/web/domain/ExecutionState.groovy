package uniandes.unacloud.web.domain

import groovy.transform.Immutable;
import uniandes.unacloud.share.enums.ExecutionStateEnum

/**
 * Class to represent states in graph execution state diagram
 * @author CesarF
 *
 */
@Immutable
class ExecutionState {
	
	/**
	 * State name 
	 */
	ExecutionStateEnum state
	
	/**
	 * Next state
	 */
	ExecutionState next
	
	/**
	 * Next state by request
	 */
	ExecutionState nextRequested
	
	/**
	 * Next state by control time out
	 */
	ExecutionState nextControl
	
	/**
	 * Time out in state
	 */
	long controlTime 
	
	/**
	 * Message in case control time out is reached
	 */
	String controlMessage

    static constraints = {		
		nextRequested nullable: true
		state nullable: false
		state unique: true
    }
}
