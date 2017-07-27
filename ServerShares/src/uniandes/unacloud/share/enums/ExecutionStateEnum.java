package uniandes.unacloud.share.enums;

/**
 * Represents state of executions
 * @author CesarF
 *
 */
public enum ExecutionStateEnum {
	
	/**
	* Execution has been requested, can't exceed 2 minutes
	 */
	REQUESTED ("REQUESTED"),
	
	/**
	 * Instance is been transmitted to physical machine, can't exceed 30 minutes
	 */
	TRANSMITTING("TRANSMITTING"),
	
	/**
	 * Execution is in configuring process, can't exceed 10 minutes
	 */
	CONFIGURING ("CONFIGURING"),
	
	/**
	 * Execution is in deploying process, can't exceed 8 minutes
	 */
	DEPLOYING ("DEPLOYING"),
	
	/**
	 * Execution is in deployed state, running up to stop time
	 */
	DEPLOYED ("DEPLOYED"),
	
	/**
	 * Execution is in failed state, should be terminate by user or by stop time
	 */
	FAILED ("FAILED"),
	
	/**
	 * Execution has been requested to be finished, can't exceed 5 minutes
	 */
	FINISHING ("FINISHING"),
	
	/**
	 * Execution is finished
	 */
	FINISHED("FINISHED"),
	
	/**
	 * Execution has been requested to be saved in server, can't exceed 4 minutes
	 */
	REQUEST_COPY("REQUEST COPY"),
	
	/**
	 * Execution is in copying process, can't exceed 30 minutes
	 */
	COPYING("COPYING"),
	
	/**
	 * Execution has not been reported for some minutes, process wait for 15 minutes to back to deployed state or move to failed
	 */
	RECONNECTING("RECONNECTING");
	
	
	/**
	 * Name of state
	 */
	public String name;
	
	/**
	 * Creates a new Execution State
	 * @param name != null
	 * @param time > 0 
	 */
	private ExecutionStateEnum(String name) {
		this.name = name;
	}
		
	/**
	 * Returns an execution state searched by name
	 * @param name of execution
	 * @return Execution state
	 */
	public static ExecutionStateEnum getEnum(String name){
		if (REQUESTED.name.equals(name) || REQUESTED.name().equals(name)) return REQUESTED;
		if (CONFIGURING.name.equals(name) || CONFIGURING.name().equals(name)) return CONFIGURING;
		if (TRANSMITTING.name.equals(name) || TRANSMITTING.name().equals(name)) return TRANSMITTING;
		if (DEPLOYING.name.equals(name) || DEPLOYING.name().equals(name)) return DEPLOYING;
		if (DEPLOYED.name.equals(name) || DEPLOYED.name().equals(name)) return DEPLOYED;
		if (FAILED.name.equals(name) || FAILED.name().equals(name)) return FAILED;
		if (FINISHING.name.equals(name) || FINISHING.name().equals(name)) return FINISHING;
		if (FINISHED.name.equals(name) || FINISHED.name().equals(name)) return FINISHED;
		if (REQUEST_COPY.name.equals(name) || REQUEST_COPY.name().equals(name)) return REQUEST_COPY;
		if (COPYING.name.equals(name) || COPYING.name().equals(name)) return COPYING;
		if (RECONNECTING.name.equals(name) || RECONNECTING.name().equals(name)) return RECONNECTING;
		return null;
	}
}
