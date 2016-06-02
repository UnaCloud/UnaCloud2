package unacloud

/**
 * Entity to represent a monitoring application configured in a virtual machine or physical machine.
 * This class is unused in this version of UnaCloud
 * @author CesarF
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
