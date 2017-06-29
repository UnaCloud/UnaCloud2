package uniandes.unacloud.common.enums;

import uniandes.unacloud.common.utils.CalendarUtils;

/**
 * Represents state of executions
 * @author CesarF
 *
 */
public enum ExecutionStateEnum {
	
	/**
	* Execution is in queue, can't exceed 2 minutes
	 */
	QUEUED ("QUEUED", CalendarUtils.MINUTE * 2),
	
	/**
	 * Execution is been downloading, can't exceed 30 minutes
	 */
	DOWNLOADING("DOWNLOADING", CalendarUtils.MINUTE * 30),
	
	/**
	 * Execution is in configuring process, can't exceed 10 minutes
	 */
	CONFIGURING ("CONFIGURING", CalendarUtils.MINUTE * 10),
	
	/**
	 * Execution is in deploying process, can't exceed 8 minutes
	 */
	DEPLOYING ("DEPLOYING", CalendarUtils.MINUTE * 8),
	
	/**
	 * Execution is in deployed state, running up to stop time
	 */
	DEPLOYED ("DEPLOYED", 0),
	
	/**
	 * Execution is in failed state, should be terminate by user or by stop time
	 */
	FAILED ("FAILED", 0),
	
	/**
	 * Execution has been requested to be finished, can't exceed 5 minutes
	 */
	FINISHING ("FINISHING", CalendarUtils.MINUTE * 5),
	
	/**
	 * Execution is finished
	 */
	FINISHED("FINISHED", 0),
	
	/**
	 * Execution has been requested to be saved in server, can't exceed 4 minutes
	 */
	REQUEST_COPY("REQUEST COPY", CalendarUtils.MINUTE * 4),
	
	/**
	 * Execution is in copying process, can't exceed 30 minutes
	 */
	COPYING("COPYING", CalendarUtils.MINUTE * 30),
	
	/**
	 * Execution has not been reported for some minutes, process wait for 15 minutes to back to deployed state or move to failed
	 */
	RECONNECTING("RECONNECTING", CalendarUtils.MINUTE * 15);//because time in validation (DEPLOYED status) is four, check control procedure
	
	/**
	 * Limit time in milliseconds for state
	 */
	private long time;
	
	/**
	 * Name of state
	 */
	public String name;
	
	/**
	 * Creates a new Execution State
	 * @param name != null
	 * @param time > 0 
	 */
	private ExecutionStateEnum(String name, long time) {
		this.name = name;
		this.time = time;
	}
	
	/**
	 * Returns limit time for state in milliseconds
	 * @return limit time
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * Returns an execution state searched by name
	 * @param name of execution
	 * @return Execution state
	 */
	public static ExecutionStateEnum getEnum(String name){
		if (QUEUED.name.equals(name) || QUEUED.name().equals(name)) return QUEUED;
		if (CONFIGURING.name.equals(name) || CONFIGURING.name().equals(name)) return CONFIGURING;
		if (DOWNLOADING.name.equals(name) || DOWNLOADING.name().equals(name)) return DOWNLOADING;
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
