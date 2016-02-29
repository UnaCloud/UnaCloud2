package unacloud.share.enums;
import unacloud.share.utils.CalendarUtils;

public enum VirtualMachineExecutionStateEnum {
	QUEUED("QUEUED",CalendarUtils.MINUTE*2),
	CONFIGURING("CONFIGURING",CalendarUtils.MINUTE*30),
	DEPLOYING("DEPLOYING",CalendarUtils.MINUTE*8),
	DEPLOYED("DEPLOYED",0),
	FAILED("FAILED",0),
	FINISHING("FINISHING",CalendarUtils.MINUTE*5),
	FINISHED("FINISHED",0),
	REQUEST_COPY("REQUEST COPY",CalendarUtils.MINUTE*2),
	COPYING("COPYING",CalendarUtils.MINUTE*30),
	RECONNECTING("RECONNECTING",CalendarUtils.MINUTE*15);//because time in validation (DEPLOYED status) is four, check control procedure
	
	private long time;
	public String name;
	
	private VirtualMachineExecutionStateEnum(String name, long time) {
		this.name = name;
		this.time = time;
	}
	
	public long getTime(){
		return time;
	}
	
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
