package unacloud.enums;

public enum DeploymentStateEnum {
	ACTIVE("ACTIVE"),FINISHED("FINISHED"),IN_PROCESS("REQUESTED");
	
	private String name;
	
	private DeploymentStateEnum(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
