package unacloud.enums;

public enum DeploymentStateEnum {
	ACTIVE("ACTIVE"),FINISHED("FINISHED");
	
	private String name;
	
	private DeploymentStateEnum(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
