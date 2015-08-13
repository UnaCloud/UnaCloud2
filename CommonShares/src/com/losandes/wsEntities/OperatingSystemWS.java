package com.losandes.wsEntities;

/**
 * Operating system entity to be transmitted on web services responses
 * @author Clouder
 */
public class OperatingSystemWS {

    private String operatingSystemType;
    private String operatingSystemName;

    public String getOperatingSystemName() {
        return operatingSystemName;
    }

    public void setOperatingSystemName(String operatingSystemName) {
        this.operatingSystemName = operatingSystemName;
    }

    public String getOperatingSystemType() {
        return operatingSystemType;
    }

    public void setOperatingSystemType(String operatingSystemType) {
        this.operatingSystemType = operatingSystemType;
    }
    
}
