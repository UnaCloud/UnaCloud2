package unacloud2.enums;

public enum ExternalCloudTypeEnum {
	COMPUTING("Computing"), STORAGE("Storage");
	String name;
	
	private ExternalCloudTypeEnum(String name) {
		this.name= name;
	}
	public String getName(){
		return name;
	}
}
