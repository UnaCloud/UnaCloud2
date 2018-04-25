package VO;

import java.util.Map;

public class HardwareProfileNodeList {

    private Map<Integer,Map<String,String>> profiles;

    public HardwareProfileNodeList(Map<Integer, Map<String, String>> profiles) {
        this.profiles = profiles;
    }

    public Map<Integer, Map<String, String>> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<Integer, Map<String, String>> profiles) {
        this.profiles = profiles;
    }
}
