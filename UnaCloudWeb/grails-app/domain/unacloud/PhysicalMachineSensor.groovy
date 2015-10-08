package unacloud

import unacloud.enums.MonitoringStatus

/**
 * 
 * @author Cesar
 *
 * Representation of sensor in physical machine or virtual machine
 */
class PhysicalMachineSensor {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	/**
	 * Status of monitoring in physicalMachine
	 */
	MonitoringStatus status
	
	/**
	 * Sensor to be configured in execution machine
	 */
	MonitorSensor sensor
	
	/**
	 * register data time in seconds 
	 */
	long frecuencySensor
	
	/**
	 * Monitor system where is located
	 */
	static belongsTo = [system: MonitorSystem]

    static constraints = {
    }
}
