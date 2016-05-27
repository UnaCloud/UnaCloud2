package unacloud.enums;

/**
 * Represents Monitoring System status
 * This states are base in state machine graph for monitoring status
 * @author CesarF
 *
 */
public enum MonitoringStatus {
	/**
	 * Monitoring is running
	 */
	RUNNING("RUNNING"), 
	/**
	 * Monitoring is stopped 
	 */
	STOPPED("STOPPED"), 
	/**
	 * Monitoring is off in physical machine
	 */
	OFF("OFF"), 
	/**
	 * Monitoring is configuring in Physical Machine
	 */
	INIT("INITIALIZING"),
	/**
	 * Monitoring is disabled
	 */
	DISABLE("DISABLED"),
	/**
	 * Monitoring show error in execution
	 */
	ERROR("ERROR");
	
	private String title;
	
	private MonitoringStatus(String title) {
		this.title = title;
	}
	
	/**
	 * Return name of monitoring status
	 * @return String title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Validates which status is requested by parameters
	 * @param title, name of status
	 * @return status or null
	 */
	public static MonitoringStatus getEnum(String title){
		if(RUNNING.getTitle().equals(title))return RUNNING;
		else if(STOPPED.getTitle().equals(title))return STOPPED;
		else if(INIT.getTitle().equals(title))return INIT;
		else if(DISABLE.getTitle().equals(title))return DISABLE;
		else if(OFF.getTitle().equals(title))return OFF;
		else if(ERROR.getTitle().equals(title))return ERROR;
		else return ERROR;
	}
}
