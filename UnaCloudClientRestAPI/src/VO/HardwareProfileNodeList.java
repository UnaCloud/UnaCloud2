package VO;

import java.util.Map;

public class HardwareProfileNodeList {

    private Map<Integer,Map<String,Integer>> profiles;

    public HardwareProfileNodeList(Map<Integer, Map<String, Integer>> profiles) {
        this.profiles = profiles;
    }

    public Map<Integer, Map<String, Integer>> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<Integer, Map<String, Integer>> profiles) {
        this.profiles = profiles;
    }
}
