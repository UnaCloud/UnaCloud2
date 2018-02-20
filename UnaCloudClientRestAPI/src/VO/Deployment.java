package VO;

import java.util.List;

public class Deployment {

    public int id;

    public ObjectId<Integer> cluster;

    public long duration;

    private List<ObjectId<Integer>> images;

    private String startTime;

    private ObjectId<String> status;

    private ObjectId<Integer> user;


    public Deployment(String startTime, long duration, int id, ObjectId<Integer> cluster, ObjectId<Integer> user, List<ObjectId<Integer>> images, ObjectId<String> status) {
        this.startTime = startTime;
        this.duration = duration;
        this.id = id;
        this.cluster = cluster;
        this.user = user;
        this.images = images;
        this.status = status;
    }
}
