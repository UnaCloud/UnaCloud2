package unacloud.enums;

import unacloud.utils.CalendarUtils;

public enum VirtualMachineExecutionStateEnum {
	REQUESTED("REQUESTED",CalendarUtils.MINUTE*2),
	CONFIGURING("CONFIGURING",CalendarUtils.MINUTE*30),
	DEPLOYING("DEPLOYING",CalendarUtils.MINUTE*4),
	DEPLOYED("DEPLOYED",0),
	FAILED("FAILED",0),
	REQUEST_FINISH("TO FINISH",CalendarUtils.MINUTE*2),
	FINISHING("FINISHING",CalendarUtils.MINUTE*4),
	FINISHED("FINISHED",0),
	REQUEST_COPY("TO COPY",CalendarUtils.MINUTE*2),
	COPYING("COPYING",CalendarUtils.MINUTE*30),
	RECONNECTING("RECONNECTING",CalendarUtils.MINUTE*15);
	
	private long time;
	public String name;
	
	private VirtualMachineExecutionStateEnum(String name, long time) {
		this.name = name;
		this.time = time;
	}
	
	public long getTime(){
		return time;
	}
			
}
