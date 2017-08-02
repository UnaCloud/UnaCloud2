package uniandes.unacloud.common.enums;

/**
 * Represents different types of transmission to send images
 * @author CesarF
 *
 */
public enum TransmissionProtocolEnum {
	
	/**
	 * P2P protocol
	 */
	P2P,
	
	/**
	 * TCP protocol
	 */
	TCP;
	
	/**
	 * Return enum using string name
	 * @param name
	 * @return enum
	 */
	public static TransmissionProtocolEnum getEnum(String name) {
		if (name.equals(P2P.name())) return P2P;
		if (name.equals(TCP.name())) return TCP;
		return null;
	}
	
	/**
	 * Returns a list of current transmission available protocols
	 * @return
	 */
	public static String[] list() {
		return new String[] {
				P2P.name(),
				TCP.name()};
	}

}
