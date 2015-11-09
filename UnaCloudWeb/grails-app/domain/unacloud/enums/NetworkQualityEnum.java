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
	
	public static NetworkQualityEnum getNetworkQuality(String name){
		if(ETHERNET10MBPS.name.equals(name))return ETHERNET10MBPS;
		if(ETHERNET100MBPS.name.equals(name))return ETHERNET100MBPS;
		if(ETHERNET1GBPS.name.equals(name))return ETHERNET1GBPS;
		if(ETHERNET10GBPS.name.equals(name))return ETHERNET10GBPS;
		if(INFINIBAND.name.equals(name))return INFINIBAND;
		if(FIBERCHANNEL.name.equals(name))return FIBERCHANNEL;
		return null;
	}
	
	public String getName() {
		return name;
	}
	
}
