package VO;

import java.util.ArrayList;
import java.util.List;

/**
 * Deployment stop request VO
 *  @author s.guzmanm
 */
public class DeploymentStopRequest {

    private List<ObjectId<Integer>> executions;


    //------------
    //Constructor
    //------------
    public DeploymentStopRequest()
    {
        executions=new ArrayList<>();
    }

    //---------
    //Getters and setters
    //------------
    public List<ObjectId<Integer>> getExecutions() {
        return executions;
    }

    public void setExecutions(List<ObjectId<Integer>> executions) {
        this.executions = executions;
    }
    //--------------
    //Methods
    //--------------

    /**
     * Adds execution to the list.
     * @param id Id of execution
     */
    public void addExecution(int id)
    {
        executions.add(new ObjectId<>(id));
    }


}
