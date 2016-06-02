package unacloud

import unacloud.enums.MonitoringStatus

/**
 * Entity to represent a monitoring application 
 * This class is unused in this version of UnaCloud
 * @author CesarF
 *
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
