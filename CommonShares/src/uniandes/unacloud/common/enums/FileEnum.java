package uniandes.unacloud.common.enums;

public enum FileEnum {
	LOG,
	MONITORING,
	OTHER,
	IMAGE;
	
	public static FileEnum getFileEnum(String name) {
		if(name.equals(LOG.name()))return LOG;
		if(name.equals(MONITORING.name()))return MONITORING;
		if(name.equals(OTHER.name()))return OTHER;
		if(name.equals(IMAGE.name()))return IMAGE;
		return null;
	}
}
