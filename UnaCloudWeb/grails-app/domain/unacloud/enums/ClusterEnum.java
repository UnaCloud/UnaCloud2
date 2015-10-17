package unacloud.enums;

public enum ClusterEnum {

	UNAVAILABLE("UNAVAILABLE"),DISABLE("DISABLE"),AVAILABLE("AVAILABLE"),FREEZE("FREEZE");
	
	String name;
	
	private ClusterEnum(String name){
		this.name = name;
	}
}
