package com.losandes.enums;

import com.losandes.utils.CalendarUtils;

/**
 * Represents state of virtual machine executions
 * @author CesarF
 *
 */
public enum VirtualMachineExecutionStateEnum {
	/**
	 * Execution has a task in queue, can't exceed 2 minutes
	 */
	QUEUED("QUEUED",CalendarUtils.MINUTE*2),
	/**
	 * Execution is in configuring process, can't exceed 30 minutes
	 */
	CONFIGURING("CONFIGURING",CalendarUtils.MINUTE*30),
	/**
	 * Execution is in deploying process, can't exceed 8 minutes
	 */
	DEPLOYING("DEPLOYING",CalendarUtils.MINUTE*8),
	/**
	 * Execution is in deployed state, running up to stop time
	 */
	DEPLOYED("DEPLOYED",0),
	/**
	 * Execution is in failed state, should be terminate by user or by stop time
	 */
	FAILED("FAILED",0),
	/**
	 * Execution has been requesting to be finished, can't exceed 5 minutes
	 */
	FINISHING("FINISHING",CalendarUtils.MINUTE*5),
	/**
	 * Execution is finished
	 */
	FINISHED("FINISHED",0),
	/**
	 * Execution has been requesting to be save in server, can't exceed 4 minutes
	 */
	REQUEST_COPY("REQUEST COPY",CalendarUtils.MINUTE*4),
	/**
	 * Execution is in copying process, can't exceed 30 minutes
	 */
	COPYING("COPYING",CalendarUtils.MINUTE*30),
	/**
	 * Execution has not been reported for some minutes, process wait for 15 minutes to back to deployed state or move to failed
	 */
	RECONNECTING("RECONNECTING",CalendarUtils.MINUTE*15);//because time in validation (DEPLOYED status) is four, check control procedure
	
	private long time;
	public String name;
	
	private VirtualMachineExecutionStateEnum(String name, long time) {
		this.name = name;
		this.time = time;
	}
	
	/**
	 * Limit time for state
	 * @return limit time
	 */
	public long getTime(){
		return time;
	}
	
	/**
	 * Returns a virtual machine execution state searched by name
	 * @param name of virtual machine execution
	 * @return Virtual Machine execution state
	 */
	public static VirtualMachineExecutionStateEnum getEnum(String name){
		if(QUEUED.name.equals(name)||QUEUED.name().equals(name))return QUEUED;
		if(CONFIGURING.name.equals(name)||CONFIGURING.name().equals(name))return CONFIGURING;
		if(DEPLOYING.name.equals(name)||DEPLOYING.name().equals(name))return DEPLOYING;
		if(DEPLOYED.name.equals(name)||DEPLOYED.name().equals(name))return DEPLOYED;
		if(FAILED.name.equals(name)||FAILED.name().equals(name))return FAILED;
		if(FINISHING.name.equals(name)||FINISHING.name().equals(name))return FINISHING;
		if(FINISHED.name.equals(name)||FINISHED.name().equals(name))return FINISHED;
		if(REQUEST_COPY.name.equals(name)||REQUEST_COPY.name().equals(name))return REQUEST_COPY;
		if(COPYING.name.equals(name)||COPYING.name().equals(name))return COPYING;
		if(RECONNECTING.name.equals(name)||RECONNECTING.name().equals(name))return RECONNECTING;
		return null;
	}
}
