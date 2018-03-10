package VO;

import java.util.ArrayList;
import java.util.List;

/**
 * Laboratory update request VO
 * @author s.guzmanm
 */
public class LaboratoryUpdateRequest {
    //Laboratory id
    private int id;
    //Given process
    private String process;
    //List of machines
    private List<ObjectId<Integer>> machines;

    //------------------
    //Constructor
    //------------------


    public LaboratoryUpdateRequest(int id, TaskManagerState process) {
        this.id = id;
        if(process!=null)
            this.process = (process+"").toLowerCase();
        machines=new ArrayList<>();
    }

    //---------------
    //Getters and Setters
    //---------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public List<ObjectId<Integer>> getMachines() {
        return machines;
    }

    public void setMachines(List<ObjectId<Integer>> machines) {
        this.machines = machines;
    }

    //----------------
    //Methods
    //---------------
    public void addMachine(int id)
    {
        machines.add(new ObjectId<>(id));
    }
}
