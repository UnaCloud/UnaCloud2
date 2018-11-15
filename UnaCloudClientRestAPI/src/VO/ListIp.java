package VO;

import java.util.List;

public class ListIp {

    private List<String> ips;

    //------------------
    //Getter and setter
    //------------------
    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public ListIp(List<String> ips) {
        this.ips = ips;
    }
}
