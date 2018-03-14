package Connection;

import VO.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Class used to manipulate deployment connections
 *  @author s.guzmanm
 */
public class DeploymentManager {

    //-----------------
    //Constants
    //-----------------
    private static final String RUTA = "/rest/deployments";

    //Constant for execution states
    public static final int DEPLOYED=5;

    public static final int FAILED=2;

    //Constants for hardware profiles
    public static final int HW_SMALL=1;

    public static final int HW_MEDIUM=2;

    public static final int HW_LARGE=3;

    public static final int HW_XLARGE=4;
    //Constants for deployment states
    public static final String FINISHED="FINISHED";


    //Attribute for UnaCloudConnection
    private UnaCloudConnection uc;
    //Attribute for gson connection for parsing
    private Gson gson;

    /**
     * Creates new deployment connection with the given UnaCloudConnection and a gson with desired json mapping criteria
     *
     * @param uc
     */
    public DeploymentManager(UnaCloudConnection uc) {
        this.uc = uc;
        gson = new GsonBuilder().setExclusionStrategies(new CustomExclusionStrategy()).create();

    }

    /**
     * Gets all the deployments of the user found in UnaCloudConnection
     *
     * @return list of active user deployments
     * @throws Exception If there is any issue during the http request
     */
    public List<DeploymentResponse> getDeployments() throws Exception {
        String jsonResponse = uc.getInfoFromUrl(RestVerb.GET, RUTA, null);
        //Do mapping to json with gson library
        Type collectionType = new TypeToken<Collection<DeploymentResponse>>() {
        }.getType();
        return gson.fromJson(jsonResponse, collectionType);
    }
    /**
     * Gets specific deployment of the user found in UnaCloudConnection
     * @param id Id of deployment
     * @return active user deployment
     * @throws Exception If there is any issue during the http request
     */
    public DeploymentResponse getDeployment(int id) throws Exception {
        String jsonResponse = uc.getInfoFromUrl(RestVerb.GET, RUTA+"/"+id, null);
        //Do mapping to json with gson library
        return gson.fromJson(jsonResponse, DeploymentResponse.class);
    }


    /**
     * Creates deployment with the given request body
     *
     * @param deploymentRequest The body of the request
     * @return Id of the deployment
     * @throws Exception If there is any execution exception (http ones mainly)
     */
    public Double deployWithParams(DeploymentRequest deploymentRequest) throws Exception {
        String jsonResponse = uc.getInfoFromUrl(RestVerb.POST, RUTA, new JSONObject(gson.toJson(deploymentRequest)));
        System.out.println(jsonResponse);
        return (Double) gson.fromJson(jsonResponse, ObjectId.class).getId();
    }

    /**
     * Stops the given deployments in execution
     *
     * @param deploymentStopRequest The deployment stop request
     * @throws Exception If there are mistakes during the request transmission
     */
    public void stopExecutions(DeploymentStopRequest deploymentStopRequest) throws Exception {
        String jsonResponse = uc.getInfoFromUrl(RestVerb.PUT, RUTA, new JSONObject(gson.toJson(deploymentStopRequest)));
        System.out.println(jsonResponse);
    }

    /**
     * Get the required execution by id
     *
     * @param idDeployment Deployment id
     * @param idExecution  Execution id
     * @throws Exception If there are mistakes during the request transmission
     */
    public ExecutionResponse getExecutionById(int idDeployment, int idExecution) throws Exception {
        String jsonResponse = uc.getInfoFromUrl(RestVerb.GET, RUTA + "/" + idDeployment + "/executions/" + idExecution, null);
        System.out.println(jsonResponse);
        return gson.fromJson(jsonResponse,ExecutionResponse.class);
    }
    /**
     * Get the required executions by id of deployed image
     *
     * @param idDeployment Deployment id
     * @param imageId  Execution id
     * @throws Exception If there are mistakes during the request transmission
     */
    public List<ExecutionResponse> getExecutionsByDeployedImageId(int idDeployment, int imageId) throws Exception {
        String jsonResponse = uc.getInfoFromUrl(RestVerb.GET, RUTA + "/" + idDeployment + "/deployedImages/" + imageId, null);
        System.out.println(jsonResponse);
        Type collectionType = new TypeToken<Collection<ExecutionResponse>>() {
        }.getType();
        return gson.fromJson(jsonResponse,collectionType);
    }
}
