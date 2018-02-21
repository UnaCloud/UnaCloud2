package Connection;

import VO.DeploymentRequest;
import VO.DeploymentResponse;
import VO.ExceptionMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Class used to manipulate deployment connections
 */
public class DeploymentManager {
    //Attribute for UnaCloudConnection
    private UnaCloudConnection uc;
    //Attribute for gson connection for parsing
    private Gson gson;

    /**
     * Creates new deployment connection with the given UnaCloudConnection and a gson with desired json mapping criteria
     * @param uc
     */
    public DeploymentManager(UnaCloudConnection uc)
    {
        this.uc=uc;
        gson = new GsonBuilder().setExclusionStrategies(new CustomExclusionStrategy()).create();

    }

    /**
     * Gets all the deployments of the user found in UnaCloudConnection
     * @return list of active user deployments
     * @throws Exception If there is any issue during the http request
     */
    public List<DeploymentResponse> getDeployments() throws Exception
    {
        String jsonResponse=uc.getInfoFromUrl(RestVerb.GET,"/UnaCloudWeb/rest/deployment",null);
        //Do mapping to json with gson library
        Type collectionType = new TypeToken<Collection<DeploymentResponse>>(){}.getType();
        return gson.fromJson(jsonResponse, collectionType);


    }

    public void deployWithParams(DeploymentRequest deploymentRequest) throws Exception
    {
        System.out.println(gson.toJson(deploymentRequest));
        String jsonResponse=uc.getInfoFromUrl(RestVerb.POST,"/UnaCloudWeb/rest/deployment",new JSONObject(gson.toJson(deploymentRequest)));
        System.out.println(jsonResponse);
    }
}
