package unacloud.enums;

public enum NetworkQualityEnum {
	ETHERNET10MBPS("ETHERNET10MBPS"),ETHERNET100MBPS("ETHERNET100MBPS"),
	ETHERNET1GBPS("ETHERNET1GBPS"),ETHERNET10GBPS("ETHERNET10GBPS"),
	INFINIBAND("INFINIBAND"),FIBERCHANNEL("FIBERCHANNEL");
	
	private String name;
	
	NetworkQualityEnum(String n){
		name = n;
	}
	
	public static String[] getConfigurations(){
		return new String[]{ETHERNET10MBPS.name,ETHERNET100MBPS.name,ETHERNET1GBPS.name,
				ETHERNET10GBPS.name,INFINIBAND.name, FIBERCHANNEL.name};
	}
}
