package unacloud
/**
 * Entity to represent a Monitoring system.
 * This class is unused in this version of UnaCloud
 * @author CesarF
 *
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
	 * 0 is every second
	 */
	int times
	
	/**
	 * State of monitoring
	 */
	boolean disable = true;
	
	/**
	 * configured Sensors in system
	 */
	static hasMany = [sensors: PhysicalMachineSensor]
	 
	
    static constraints = {
    }
}
