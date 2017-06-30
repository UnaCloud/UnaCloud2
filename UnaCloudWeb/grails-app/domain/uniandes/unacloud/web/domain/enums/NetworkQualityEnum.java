package uniandes.unacloud.web.domain.enums;

/**
 * Represents types of Network Quality 
 * @author CesarF
 *
 */
public enum NetworkQualityEnum {
	/**
	 * ETHERNET 10 MBPS
	 */
	ETHERNET10MBPS("ETHERNET10MBPS"),
	/**
	 * ETHERNET 100 MBPS
	 */
	ETHERNET100MBPS("ETHERNET100MBPS"),
	/**
	 * ETHERNET 1 GBPS
	 */
	ETHERNET1GBPS("ETHERNET1GBPS"),
	/**
	 * ETHERNET 10 GBPS
	 */
	ETHERNET10GBPS("ETHERNET10GBPS"),
	/**
	 * INFINIBAND
	 */
	INFINIBAND("INFINIBAND"),
	/**
	 * FIBER CHANNEL
	 */
	FIBERCHANNEL("FIBERCHANNEL");
	
	private String name;
	
	NetworkQualityEnum(String n) {
		name = n;
	}
	
	/**
	 * Returns a list of all network quality values for physical machines
	 * @return String list with configurations
	 */
	public static String[] getConfigurations() {
		return new String[]{
				ETHERNET10MBPS.name,
				ETHERNET100MBPS.name,
				ETHERNET1GBPS.name,
				ETHERNET10GBPS.name,
				INFINIBAND.name, 
				FIBERCHANNEL.name};
	}
	
	/**
	 * Returns the network quality requested by parameter
	 * @param name to be search
	 * @return Network Quality value
	 */
	public static NetworkQualityEnum getNetworkQuality(String name) {
		if (ETHERNET10MBPS.name.equals(name)) return ETHERNET10MBPS;
		if (ETHERNET100MBPS.name.equals(name)) return ETHERNET100MBPS;
		if (ETHERNET1GBPS.name.equals(name)) return ETHERNET1GBPS;
		if (ETHERNET10GBPS.name.equals(name)) return ETHERNET10GBPS;
		if (INFINIBAND.name.equals(name)) return INFINIBAND;
		if (FIBERCHANNEL.name.equals(name)) return FIBERCHANNEL;
		return null;
	}
	
	/**
	 * Returns name 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
}
