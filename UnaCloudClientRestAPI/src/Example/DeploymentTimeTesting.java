package Example;

import Connection.*;
import VO.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for deployment time testing examples with different methods.
 * @pauthor s.guzmanm
 */
public class DeploymentTimeTesting {
    //Given UnaCloudConnection
    private UnaCloudConnection uc;

    public DeploymentTimeTesting(UnaCloudConnection uc) {
        this.uc = uc;
    }

    public UnaCloudConnection getUc() {
        return uc;
    }

    public void setUc(UnaCloudConnection uc) {
        this.uc = uc;
    }

    /**
     * Method for the tests deployed by Jesse.
     * @throws Exception If there is any HTTPException whatsoever
     */
    public void jesseTest() throws Exception
    {
        int maxCleanableMachines=10;
        //Clean the cache of the given machines or of given numbers.
        System.out.println("Cache");
        //The user must know the id of the lab to clean up and get machines from
        LaboratoryManager lab=new LaboratoryManager(uc);
        //Get lab physical machines for cleaning cache
        List<PhysicalMachineResponse> list=lab.getLaboratoryMachines(2);
        //Sort by id
        Collections.sort(list);
        //Clean cache of given machines until they reach a top desired by the user
        LaboratoryUpdateRequest laboratoryUpdateRequest=new LaboratoryUpdateRequest(2, TaskManagerState.CACHE);
        int i=0;
        for(PhysicalMachineResponse phy:list)
        {
            System.out.println("MACHINE "+phy.getId());
            laboratoryUpdateRequest.addMachine(phy.getId());
            i++;
            if(i>=maxCleanableMachines)
                break;
        }
        //TODO: Find out WTF happened with the laboratory service of clean cache
        //lab.cleanCache(laboratoryUpdateRequest);
        //Check that every machine has finished cleaning cache before deployment
        boolean terminaCache=false;
        while(!terminaCache)
        {
            Thread.sleep(60000);
            list=lab.getLaboratoryMachines(2);
            Collections.sort(list);
            terminaCache=true;
            i=0;
            for(PhysicalMachineResponse phy:list)
            {
                System.out.println("Mac "+phy.getId());
                if(phy.getState().getName().equals(LaboratoryManager.MACHINE_STATE.PROCESSING+""))
                {
                    terminaCache=false;
                    break;
                }
                i++;
                if(i>=maxCleanableMachines)
                    break;
            }
        }
        DeploymentManager dep= new DeploymentManager(uc);
        //Post deployment with params
        DeploymentRequest deploymentRequest=new DeploymentRequest(3600000,61);
        deploymentRequest.addNode(74,1,1,"UnaCloudConnection2Test",true);
        double deploymentId=dep.deployWithParams(deploymentRequest);
        System.out.println("ID DEPLOY"+deploymentId);

        //Signals if you need to throw an exception or not
        boolean lanzaException=false;
        //Get the current deployment
        DeploymentResponse deploy=dep.getDeployment((int)deploymentId);
        System.out.println(deploy.getStatus().getName()+"");
        //Get given deployment executions
        System.out.println("Get executions");
        boolean todoEstaDetenido=false;
        int state=0;
        while(!todoEstaDetenido)
        {
            Thread.sleep(60000);
            todoEstaDetenido=true;
            for(ObjectId<Integer> id:deploy.getImages())
            {
                for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                {
                    state=exec.getState().getId();
                    System.out.println("EXEC "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                    if(state!=DeploymentManager.DEPLOYED && state!=DeploymentManager.FAILED)
                    {
                        todoEstaDetenido=false;
                        break;
                    }
                    if(state== DeploymentManager.FAILED)
                        lanzaException=true;
                }
            }
        }
        System.out.println("Stop");
        DeploymentStopRequest deploymentStopRequest=new DeploymentStopRequest();
        //Cycle through all executions of the deployment and add them to the list for stopping
        for(ObjectId<Integer> id:deploy.getImages())
        {
            for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
            {
                System.out.println("ADD "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                deploymentStopRequest.addExecution(exec.getId());
            }
        }
        //Stop executions
        dep.stopExecutions(deploymentStopRequest);
        //Throws exception if there is a failed instance of deployment
        if(lanzaException)
            throw new Exception("There are failed deployment instances");
    }
    /**
     * Methods for tests required by Cesar.
     * @throws Exception If there is any HTTPException or whatsoever
     */
    public void cesarTest() throws Exception
    {
        DeploymentManager dep= new DeploymentManager(uc);

        //Post deployment with params
        DeploymentRequest deploymentRequest=new DeploymentRequest(2,2);
        deploymentRequest.addNode(13,1,2,"MyHost2",false);
        double deploymentId=dep.deployWithParams(deploymentRequest);
        System.out.println("ID DEPLOY"+deploymentId);

        //Get the current deployment
        DeploymentResponse deploy=dep.getDeployment((int)deploymentId);
        System.out.println(deploy.getStatus().getName()+"");

        //Signals if you need to throw an exception or not
        boolean lanzaException=false;
        //Assume we have executions
        System.out.println("Get executions");
        boolean todoEstaDetenido=false;
        int state=0;
        while(!todoEstaDetenido)
        {
            Thread.sleep(60000);
            todoEstaDetenido=true;
            for(ObjectId<Integer> id:deploy.getImages())
            {
                for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                {
                    state=exec.getState().getId();
                    System.out.println("EXEC "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                    if(state!=DeploymentManager.DEPLOYED && state!=DeploymentManager.FAILED)
                    {
                        todoEstaDetenido=false;
                        break;
                    }
                    if(state== DeploymentManager.FAILED)
                        lanzaException=true;
                }
            }
        }


        ArrayList<Integer> machines=new ArrayList<>();
        System.out.println("Stop");
        DeploymentStopRequest deploymentStopRequest=new DeploymentStopRequest();
        //Cycle through all executions of the deployment and add them to the list for stopping
        for(ObjectId<Integer> id:deploy.getImages())
        {
            for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
            {
                System.out.println("ADD "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                deploymentStopRequest.addExecution(exec.getId());
                machines.add(exec.getExecutionNode().getId());
            }
        }

        //Stop executions
        dep.stopExecutions(deploymentStopRequest);

        //Throws exception if there is a failed instance of deployment
        if(lanzaException)
            throw new Exception("There are failed deployment instances");

        //Clean the cache of the given machines or of given numbers.
        System.out.println("Cache");

        //The user must know the id of the lab to clean up
        LaboratoryManager lab=new LaboratoryManager(uc);
        LaboratoryUpdateRequest laboratoryUpdateRequest=new LaboratoryUpdateRequest(1, TaskManagerState.CACHE);
        for(Integer i:machines)
        {
            System.out.println("MACHINE "+i);
            laboratoryUpdateRequest.addMachine(i);
        }
        lab.cleanCache(laboratoryUpdateRequest);
    }
    /**
     * Test for main.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        UnaCloudConnection uc = new UnaCloudConnection("5ZVAZEP0Q7RQRYK2LXYON05T7LUA9GOI","http://157.253.236.113:8080/UnaCloud");
        DeploymentTimeTesting deploymentTimeTesting=new DeploymentTimeTesting(uc);
        deploymentTimeTesting.jesseTest();
    }



}
