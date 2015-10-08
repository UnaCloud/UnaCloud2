package unacloud.enums;

public enum MonitoringStatus {
	RUNNING("Running"), STOPPED("Stopping"), OFF("Off"), INIT("Initializing"),DISABLE("Disable"),ERROR("Error");
	
	private String title;
	
	private MonitoringStatus(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
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
