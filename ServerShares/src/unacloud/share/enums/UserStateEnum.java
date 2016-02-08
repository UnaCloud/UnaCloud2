package unacloud.share.enums;

public enum UserStateEnum {
	
	AVAILABLE("AVAILABLE"), DISABLE("DISABLE"), BLOCKED("BLOCKED");
	
	private String name;
	
	private UserStateEnum(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static UserStateEnum getEnum(String name){
		if(name.equals(AVAILABLE.name)||name.equals(AVAILABLE.name()))return AVAILABLE;
		if(name.equals(DISABLE.name)||name.equals(DISABLE.name()))return DISABLE;
		if(name.equals(BLOCKED.name)||name.equals(BLOCKED.name()))return BLOCKED;
		return null;
	}
}
