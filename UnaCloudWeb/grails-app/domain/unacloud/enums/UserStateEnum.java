package unacloud.enums;

public enum UserStateEnum {
	
	AVAILABLE("AVAILABLE"), DISABLE("DISABLE"), BLOCKED("BLOCKED");
	
	private String name;
	
	private UserStateEnum(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
