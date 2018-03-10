package VO;

import java.util.Date;
import java.util.List;

/**
 * Execution responde VO
 * @author s.guzmanm
 */
public class ExecutionResponse {
    //Execution id
    private int id;
     //Image id where files of this instance will be saved
     //This id is optional
    private int copyTo;
    //Deploy image
    private ObjectId<Integer> deployImage;
    //Duration
    private long duration;
    //Execution node or physical machine
    private ObjectId<Integer> executionNode;
    //The given hardware profile
    private ObjectId<Integer> hardwareProfile;
    //The used interfaces
    private List<ObjectId<Integer>> interfaces;
    //Last report
    private String lastReport;
    //message
    private String message;
    //name
    private String name;
    //start time
    private Date startTime;
    //State
    private ObjectId<Integer> state;
    //stop time
    private Date stopTime;

    //-----------
    //Constructor
    //-----------
    public ExecutionResponse(int id, int copyTo, ObjectId<Integer> deployImage, long duration, ObjectId<Integer> executionNode, ObjectId<Integer> hardwareProfile, List<ObjectId<Integer>> interfaces, String lastReport, String message, String name, Date startTime, ObjectId<Integer> state, Date stopTime) {
        this.id = id;
        this.copyTo = copyTo;
        this.deployImage = deployImage;
        this.duration = duration;
        this.executionNode = executionNode;
        this.hardwareProfile = hardwareProfile;
        this.interfaces = interfaces;
        this.lastReport = lastReport;
        this.message = message;
        this.name = name;
        this.startTime = startTime;
        this.state = state;
        this.stopTime = stopTime;
    }
    //---------------------
    //Getters and setters
    //---------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(int copyTo) {
        this.copyTo = copyTo;
    }

    public ObjectId<Integer> getDeployImage() {
        return deployImage;
    }

    public void setDeployImage(ObjectId<Integer> deployImage) {
        this.deployImage = deployImage;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public ObjectId<Integer> getExecutionNode() {
        return executionNode;
    }

    public void setExecutionNode(ObjectId<Integer> executionNode) {
        this.executionNode = executionNode;
    }

    public ObjectId<Integer> getHardwareProfile() {
        return hardwareProfile;
    }

    public void setHardwareProfile(ObjectId<Integer> hardwareProfile) {
        this.hardwareProfile = hardwareProfile;
    }

    public List<ObjectId<Integer>> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<ObjectId<Integer>> interfaces) {
        this.interfaces = interfaces;
    }

    public String getLastReport() {
        return lastReport;
    }

    public void setLastReport(String lastReport) {
        this.lastReport = lastReport;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public ObjectId<Integer> getState() {
        return state;
    }

    public void setState(ObjectId<Integer> state) {
        this.state = state;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
}
