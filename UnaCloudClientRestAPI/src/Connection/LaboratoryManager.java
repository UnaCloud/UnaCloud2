package Connection;

import VO.ExecutionResponse;
import VO.LaboratoryUpdateRequest;
import VO.PhysicalMachineResponse;
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
public class LaboratoryManager {
    //Enum for knowing machine states
    public enum MACHINE_STATE{
        ON,OFF,PROCESSING,DISABLED
    }
    //Constants
    private static final String RUTA="/rest/laboratories";

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

    /**
     * Get machines of a given laboratory.
     * @param id
     * @return List with machines of given laboratory
     */
    public List<PhysicalMachineResponse> getLaboratoryMachines(int id) throws Exception
    {
        String jsonResponse = uc.getInfoFromUrl(RestVerb.GET, RUTA + "/" + id + "/machines", null);
        System.out.println(jsonResponse);
        Type collectionType = new TypeToken<Collection<PhysicalMachineResponse>>() {
        }.getType();
        return gson.fromJson(jsonResponse,collectionType);
    }
}
