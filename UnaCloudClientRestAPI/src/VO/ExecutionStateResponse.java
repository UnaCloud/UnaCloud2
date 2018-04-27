package VO;

public class ExecutionStateResponse {

    //Id
    private int id;
    //Change time
    private String changeTime;
    //Execution
    private ObjectId<Integer> execution;
    //Message
    private String message;
    //State
    private ObjectId<Integer> state;

    public ExecutionStateResponse(int id, String changeTime, ObjectId<Integer> execution, String message, ObjectId<Integer> state) {
        this.id = id;
        this.changeTime = changeTime;
        this.execution = execution;
        this.message = message;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    public ObjectId<Integer> getExecution() {
        return execution;
    }

    public void setExecution(ObjectId<Integer> execution) {
        this.execution = execution;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ObjectId<Integer> getState() {
        return state;
    }

    public void setState(ObjectId<Integer> state) {
        this.state = state;
    }
}
