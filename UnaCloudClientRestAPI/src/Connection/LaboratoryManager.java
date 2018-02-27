package Connection;

import VO.LaboratoryUpdateRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

/**
 * Class used to manipulate deployment connections
 */
public class LaboratoryManager {
    //Constants
    private static final String RUTA="/UnaCloudWeb/rest/laboratory";

    //Attribute for UnaCloudConnection
    private UnaCloudConnection uc;
    //Attribute for gson connection for parsing
    private Gson gson;

    /**
     * Creates new deployment connection with the given UnaCloudConnection and a gson with desired json mapping criteria
     * @param uc
     */
    public LaboratoryManager(UnaCloudConnection uc)
    {
        this.uc=uc;
        gson = new GsonBuilder().setExclusionStrategies(new CustomExclusionStrategy()).create();

    }

    /**
     * Cleans the cache of given machines
     * @param laboratoryUpdateRequest The laboratory update request
     */

    public void cleanCache(LaboratoryUpdateRequest laboratoryUpdateRequest) throws Exception
    {
        String jsonResponse=uc.getInfoFromUrl(RestVerb.PUT,RUTA,new JSONObject(gson.toJson(laboratoryUpdateRequest)));
        System.out.println(jsonResponse);

    }


    public void stopExecutions()
    {

    }

    public void getExecutionById()
    {

    }
}
