package com.andes.enums;


/**
 * Representation of execution states managed by server
 * @author Cesar
 *
 */
public enum VirtualMachineExecutionStateEnum {
	QUEQUED,
	CONFIGURING,
	DEPLOYING,
	DEPLOYED,
	FAILED,
	REQUEST_FINISH,
	FINISHING,
	FINISHED,
	REQUEST_COPY,
	COPYING,
	RECONNECTING;	
			
}
