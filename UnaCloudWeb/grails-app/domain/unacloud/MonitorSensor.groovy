package unacloud

/**
 * 
 * @author Cesar
 * 
 * representation of a sensor 
 *
 */
class MonitorSensor {	
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	/**
	 * Name of monitor
	 */
	String name;
	
	/**
	 * Description of sensor, e.g This sensor is used to get energy data
	 */
	String description;
	
	/**
	 * enable or disable sensor in the system
	 */
	boolean disable;

    static constraints = {
    }
}
