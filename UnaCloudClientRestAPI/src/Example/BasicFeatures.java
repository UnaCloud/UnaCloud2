package Example;

import Connection.DeploymentManager;
import Connection.LaboratoryManager;
import Connection.UnaCloudConnection;
import VO.*;

import java.util.Collections;
import java.util.List;

/**
 * Class for showcasing a simple deployment example.
 *  @author s.guzmanm
 */
public class BasicFeatures {
    //Given UnaCloudConnection
    private UnaCloudConnection uc;
    //------------
    //Constructor and getter and setters
    //------------
    public BasicFeatures(UnaCloudConnection uc) {
        this.uc = uc;
    }

    public UnaCloudConnection getUc() {
        return uc;
    }

    public void setUc(UnaCloudConnection uc) {
        this.uc = uc;
    }

    /**
     * Simple method for fullfilling one deployment. It also checks upon the status of the current deployment.
     * @throws Exception If there are any HTTPExceptions in the way.
     */
    public double generateSimpleDeployment() throws Exception
    {
        DeploymentManager dep= new DeploymentManager(uc);
        //Post deployment with params
        DeploymentRequest deploymentRequest=new DeploymentRequest(3600000,61);
        deploymentRequest.addNode(74,DeploymentManager.HW_SMALL,1,"My Host",false);
        return (dep.deployWithParams(deploymentRequest));

    }

    /**
     * Simple method for getting DeploymentResponse from the given id.
     * @param id Given id
     * @return DeploymentResponse of the API Rest
     * @throws Exception If tehre are any HTTP Exceptions in the way
     */
    public DeploymentResponse getDeploymentResponse(int id) throws Exception
    {
        DeploymentManager dep= new DeploymentManager(uc);
        //Get the current deployment
        return dep.getDeployment(id);
    }

    /**
     * Simple method for cleaning the cache of a given lab with a maximum number of machines.
     * @param labId Lab id
     * @param maxCleanableMachines Maximum number of cleanable machines
     * @throws Exception If there are any errors in the Http Response
     */
    public void cleanCache(int labId, int maxCleanableMachines) throws Exception
    {
        LaboratoryManager lab = new LaboratoryManager(uc);
        //Get lab physical machines for cleaning cache
        List<PhysicalMachineResponse> list = lab.getLaboratoryMachines(labId);
        //Sort by name
        Collections.sort(list);
        //Clean cache of given machines until they reach a top desired by the user
        LaboratoryUpdateRequest laboratoryUpdateRequest = new LaboratoryUpdateRequest(labId, TaskManagerState.CACHE);
        int i = 0;
        //Search the ordered list and add the machines
        for (PhysicalMachineResponse phy : list) {
            laboratoryUpdateRequest.addMachine(phy.getId());
            i++;
            if (i >= maxCleanableMachines)
                break;
        }
        lab.cleanCache(laboratoryUpdateRequest);
    }

    /**
     * Checks if the cache cleaning is finished or not.
     * @param labId Lab id
     * @param maxCleanableMachines Maximum number of cleanable machines
     * @return True if the cache cleaning is finished, false otherwise.
     * @throws Exception If there are any errors in the HttpResponse
     */
    public boolean isCacheCleaningFinished(int labId, int maxCleanableMachines) throws Exception
    {
        LaboratoryManager lab = new LaboratoryManager(uc);
        //Get lab physical machines for cleaning cache
        List<PhysicalMachineResponse> list;
        int i;
        //Check that every machine has finished cleaning cache before deployment
        list = lab.getLaboratoryMachines(labId);
        //Sort machine list by name
        Collections.sort(list);
        i = 0;
        for (PhysicalMachineResponse phy : list) {
            if (phy.getState().getName().equals(LaboratoryManager.MACHINE_STATE.PROCESSING + "")) {
                return false;
            }
            i++;
            if (i >= maxCleanableMachines)
                break;
        }
        return true;
    }

    /**
     * Stops the given deployment with the respective DeploymentResponse.
     * @param deploymentId Id of the deployment
     * @param deploy DeploymentResponse from getting the deployment with GET in the API REST
     * @throws Exception If there are any errors in the HttpResponse
     */
    public void stopDeployment(int deploymentId,DeploymentResponse deploy) throws Exception
    {
        DeploymentManager dep = new DeploymentManager(uc);
        DeploymentStopRequest deploymentStopRequest = new DeploymentStopRequest();
        //Cycle through all executions of the deployment and add them to the list for stopping
        for (ObjectId<Integer> id : deploy.getImages()) {
            for (ExecutionResponse exec : dep.getExecutionsByDeployedImageId((int) deploymentId, id.getId())) {
                deploymentStopRequest.addExecution(exec.getId());
            }
        }
        //Stop executions
        dep.stopExecutions(deploymentStopRequest);
    }

    /**
     * Finish the deployment given in the deploymentResponse object
     * @param deploy DeploymentResponse
     * @throws Exception If there are any errors in the HttpResponse
     */
    public void finishDeployment(DeploymentResponse deploy) throws Exception
    {
        DeploymentManager dep = new DeploymentManager(uc);

        boolean finished=false;
        while(!finished)
        {
            finished=true;
            for(ObjectId<Integer> image:deploy.getImages())
            {
                if(!dep.getExecutionsByDeployedImageId(deploy.getId(),image.getId()).isEmpty())
                {
                    finished=false;
                    break;
                }
            }
        }
    }
    /**
     * Method for looping until the executions are finished. It returns whether there was a successful deployment or not.
     * @param deploy The deployment response for looking at the executions
     * @return Boolean that determines whether the deployment was finished successfully (true) or it had errors during launch (false)
     */
    public boolean finishExecutions(DeploymentResponse deploy) throws Exception
    {
        DeploymentManager dep = new DeploymentManager(uc);

        int deploymentId=deploy.getId();
        boolean todoEstaDetenido=false;
        int state=0;
        while(!todoEstaDetenido)
        {
            Thread.sleep(60000);
            todoEstaDetenido=true;
            for(ObjectId<Integer> id:deploy.getImages())
            {
                for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId(deploymentId,id.getId()))
                {
                    state=exec.getState().getId();
                    System.out.println("EXEC "+exec.getId()+" "+exec.getExecutionNode().getId()+" "+exec.getState().getId());
                    if(state!=DeploymentManager.DEPLOYED && state!=DeploymentManager.FAILED)
                    {
                        todoEstaDetenido=false;
                        break;
                    }
                    if(state== DeploymentManager.FAILED)
                        return false;
                }
            }
        }
        return true;
    }
    /**
     * Test for main.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        UnaCloudConnection uc = new UnaCloudConnection("5ZVAZEP0Q7RQRYK2LXYON05T7LUA9GOI","http://157.253.236.113:8080/UnaCloud");
        BasicFeatures basicFeatures =new BasicFeatures(uc);
       //Clean cache
        basicFeatures.cleanCache(1,50);
        //Check if cache cleaning is finished
        while(!basicFeatures.isCacheCleaningFinished(1,50))
            Thread.sleep(30000);
        //Generate deployment set by default inside this method
        double deploymentId=basicFeatures.generateSimpleDeployment();
        //Get response from server
        DeploymentResponse deploymentResponse=basicFeatures.getDeploymentResponse((int)deploymentId);
        //Check if executions have finished successfully
        if(!basicFeatures.finishExecutions(deploymentResponse))
            throw new Exception("There are failed executions");
        //Stop the executions of the given deployment
        basicFeatures.stopDeployment((int)deploymentId,deploymentResponse);
        //Check that every machine of the deployment does not have any executions on going
        basicFeatures.finishDeployment(deploymentResponse);
        //Wait before next deployment
        Thread.sleep(30000);


    }
}
