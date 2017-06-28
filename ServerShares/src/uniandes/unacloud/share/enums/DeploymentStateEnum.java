package uniandes.unacloud.share.enums;

/**
 * represents states of deployment
 * @author CesarF
 *
 */
public enum DeploymentStateEnum {
	/**
	 * In case deployment has at least one execution in an state not finished
	 */
	ACTIVE("ACTIVE"),
	/**
	 * All execution in deployment has finished state
	 */
	FINISHED("FINISHED");
	
	private String name;
	
	private DeploymentStateEnum(String name) {
		this.name = name;
	}
	
	/**
	 * Return name of state
	 * @return name
	 */
	public String getName() {
		return name;
	}
}
