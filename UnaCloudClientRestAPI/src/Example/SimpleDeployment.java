package Example;

import Connection.DeploymentManager;
import Connection.UnaCloudConnection;
import VO.*;

/**
 * Class for showcasing a simple deployment example.
 *  @author s.guzmanm
 */
public class SimpleDeployment {
    //Given UnaCloudConnection
    private UnaCloudConnection uc;
    //------------
    //Constructor and getter and setters
    //------------
    public SimpleDeployment(UnaCloudConnection uc) {
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
    public void generateSimpleDeployment() throws Exception
    {
        DeploymentManager dep= new DeploymentManager(uc);
        //Post deployment with params
        DeploymentRequest deploymentRequest=new DeploymentRequest(3600000,61);
        deploymentRequest.addNode(74,DeploymentManager.HW_SMALL,1,"My Host",false);
        double deploymentId=dep.deployWithParams(deploymentRequest);
        System.out.println("ID DEPLOY"+deploymentId);

        //Get the current deployment
        DeploymentResponse deploy=dep.getDeployment((int)deploymentId);
        System.out.println(deploy.getStatus().getName()+"");

    }
    /**
     * Test for main.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        UnaCloudConnection uc = new UnaCloudConnection("5ZVAZEP0Q7RQRYK2LXYON05T7LUA9GOI","http://157.253.236.113:8080/UnaCloud");
        SimpleDeployment simpleDeployment=new SimpleDeployment(uc);
        simpleDeployment.generateSimpleDeployment();
    }
}
