package VO;

import java.util.List;

/**
 * Class that represents how a deployment response is made
 *  @author s.guzmanm
 */
public class DeploymentResponse {
    //Class for status
    public class Status{
        //Name of the status
        private String name;
        //Constructor

        public Status(String name) {
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


    //Id of deployment
    private int id;
    //Cluster object
    private ObjectId<Integer> cluster;
    //Duration
    private long duration;
    //List of images
    private List<ObjectId<Integer>> images;
    //Start time
    private String startTime;
    //Status object
    private Status status;
    //User object
    private ObjectId<Integer> user;

    //Constructor
    public DeploymentResponse(String startTime, long duration, int id, ObjectId<Integer> cluster, ObjectId<Integer> user, List<ObjectId<Integer>> images, Status status) {
        this.startTime = startTime;
        this.duration = duration;
        this.id = id;
        this.cluster = cluster;
        this.user = user;
        this.images = images;
        this.status = status;
    }
    //----------------
    //Getters and setters
    //--------------
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ObjectId<Integer> getCluster() {
        return cluster;
    }

    public void setCluster(ObjectId<Integer> cluster) {
        this.cluster = cluster;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<ObjectId<Integer>> getImages() {
        return images;
    }

    public void setImages(List<ObjectId<Integer>> images) {
        this.images = images;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ObjectId<Integer> getUser() {
        return user;
    }

    public void setUser(ObjectId<Integer> user) {
        this.user = user;
    }
}
