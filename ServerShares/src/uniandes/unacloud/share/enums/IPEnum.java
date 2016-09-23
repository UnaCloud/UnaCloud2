package uniandes.unacloud.share.enums;

/**
 * Represents IP states
 * @author CesarF
 *
 */
public enum IPEnum {
	/**
	 * IP is assign a current deployed execution
	 */
	USED,
	/**
	 * IP has been assigned to execution
	 */
	RESERVED,
	/**
	 * Disabled by admin used
	 */
	DISABLED,
	/**
	 * IP is available to be assign
	 */
	AVAILABLE;

}
