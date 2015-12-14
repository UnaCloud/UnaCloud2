package unacloud.enums;

public enum VirtualMachineExecutionStateEnum {
	REQUESTED("REQUESTED"),CONFIGURING("CONFIGURING"),DEPLOYING("DEPLOYING"),DEPLOYED("DEPLOYED"),FAILED("FAILED"),REQUEST_FINISH("TO FINISH"),FINISHING("FINISHING"),FINISHED("FINISHED"),REQUEST_COPY("TO COPY"),COPYING("COPYING");
	
	public String name;
	
	private VirtualMachineExecutionStateEnum(String name) {
		this.name = name;
	}
			
}
