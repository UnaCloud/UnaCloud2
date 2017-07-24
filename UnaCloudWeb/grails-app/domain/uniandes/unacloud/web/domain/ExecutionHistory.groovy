package uniandes.unacloud.web.domain

import java.util.Date;


/**
 * Entity to represent the state of execution in an specified time.
 * The purpose of this class is analyze changes in execution state in range time.
 * This entity is create by triggers in database (Check DatabaseService)
 *
 * @author CesarF
 */
class ExecutionHistory {
	
	/**
	 * Node state
	 */
	ExecutionState state
	
	/**
	 * Date when the node change of status
	 */
	Date changeTime
	
	/**
	 * Status message
	 */
	String message
	
	/**
	 * Execution which belongs this request
	 */
	static belongsTo = [execution:Execution]

	/**
	 * status and time never can be null
	 */
    static constraints = {
		state nullable:false
		changeTime nullable: false
		message nullable:true
    }
}
