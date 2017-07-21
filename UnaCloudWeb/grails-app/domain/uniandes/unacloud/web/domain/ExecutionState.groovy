package uniandes.unacloud.web.domain

import groovy.transform.Immutable;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

@Immutable
class ExecutionState {
	
	ExecutionStateEnum state
	
	ExecutionState next
	
	ExecutionState nextRequested
	
	ExecutionState nextControl
	
	int controlTime 

    static constraints = {		
		nextRequested nullable: true
		state nullable: false
		state unique: true
    }
}
