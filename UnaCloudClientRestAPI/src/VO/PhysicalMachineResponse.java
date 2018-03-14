package VO;

import java.util.Date;
import java.util.List;

/**
 * Physical machine response VO
 * @author s.guzmanm
 */
public class PhysicalMachineResponse implements Comparable<PhysicalMachineResponse> {



    //Class for state
    public class State{
        //Name of the status
        private String name;
        //Constructor

        public State(String name) {
            this.name = name;
        }

        //Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    //Machine id
    private int id;
    //Agent version
    private String agentVersion;
    //Cores
    private int cores;
    //Data space
    private double dataSpace;
    //Free space
    private double freeSpace;
    //High availability
    private boolean highAvailability;
    //Ip
    private ObjectId<Integer> ip;
    //Laboratory
    private ObjectId<Integer> laboratory;
    //Last report
    private Date lastReport;
    //Mac
    private String mac;
    //Name
    private String name;
    //Operating system
    private ObjectId<Integer> operatingSystem;
    //pCores
    private int pCores;
    //Platform
    private List<ObjectId<Integer>> platforms;
    //RAM
    private int ram;
    //State
    private State state;
    //Is with user
    private boolean withUser;

    //-------------------
    //Constructor
    //-------------------

    public PhysicalMachineResponse(int id, String agentVersion, int cores, double dataSpace, double freeSpace, boolean highAvailability, ObjectId<Integer> ip, ObjectId<Integer> laboratory, Date lastReport, String mac, String name, ObjectId<Integer> operatingSystem, int pCores, List<ObjectId<Integer>> platforms, int ram, State state, boolean withUser) {
        this.id = id;
        this.agentVersion = agentVersion;
        this.cores = cores;
        this.dataSpace = dataSpace;
        this.freeSpace = freeSpace;
        this.highAvailability = highAvailability;
        this.ip = ip;
        this.laboratory = laboratory;
        this.lastReport = lastReport;
        this.mac = mac;
        this.name = name;
        this.operatingSystem = operatingSystem;
        this.pCores = pCores;
        this.platforms = platforms;
        this.ram = ram;
        this.state = state;
        this.withUser = withUser;
    }

    //------------------
    //Getters and setters
    //------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public double getDataSpace() {
        return dataSpace;
    }

    public void setDataSpace(double dataSpace) {
        this.dataSpace = dataSpace;
    }

    public double getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(double freeSpace) {
        this.freeSpace = freeSpace;
    }

    public boolean isHighAvailability() {
        return highAvailability;
    }

    public void setHighAvailability(boolean highAvailability) {
        this.highAvailability = highAvailability;
    }

    public ObjectId<Integer> getIp() {
        return ip;
    }

    public void setIp(ObjectId<Integer> ip) {
        this.ip = ip;
    }

    public ObjectId<Integer> getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(ObjectId<Integer> laboratory) {
        this.laboratory = laboratory;
    }

    public Date getLastReport() {
        return lastReport;
    }

    public void setLastReport(Date lastReport) {
        this.lastReport = lastReport;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId<Integer> getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(ObjectId<Integer> operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public int getpCores() {
        return pCores;
    }

    public void setpCores(int pCores) {
        this.pCores = pCores;
    }

    public List<ObjectId<Integer>> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<ObjectId<Integer>> platforms) {
        this.platforms = platforms;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isWithUser() {
        return withUser;
    }

    public void setWithUser(boolean withUser) {
        this.withUser = withUser;
    }

    //-------
    //Methods
    //---------

    /**
     * Method for comparing by using ids.
     * @param o Other object
     * @return The comparison result
     */
    @Override
    public int compareTo(PhysicalMachineResponse o) {
        return name.compareTo(o.name);
    }

}
