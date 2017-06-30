package uniandes.unacloud.web.domain.enums;

/**
 * Represents type of external cloud service
 * unused in this UnaCloud Version
 * @author CesarF
 *
 */
public enum ExternalCloudTypeEnum {
	
	/**
	 * Computing resources services
	 */
	COMPUTING("Computing"), 
	/**
	 * Storage services
	 */
	STORAGE("Storage");
	
	String name;
	
	private ExternalCloudTypeEnum(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
