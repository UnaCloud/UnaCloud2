package unacloud

/**
 * 
 * @author Cesar
 *
 * Representation of a Monitoring System
 */
class MonitorSystem {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Name of the system
	 */
	String name
	
	/**
	 * Description of monitor system: how many sensors, which ones and purpose of them
	 */
	String description
	
	/**
	 * time in seconds during system monitoring will run
	 */
	long windowSizeRecord
	
	/**
	 * times monitoring system will execute in machine
	 * 0 is continue
	 */
	int times
	
	boolean disable
	
	/**
	 * configured Sensors in system
	 */
	static hasMany = [sensors: PhysicalMachineSensor]
	 
	
    static constraints = {
    }
}
