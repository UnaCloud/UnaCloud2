package Connection;

import VO.DeploymentExecutionResponse;
import VO.DeploymentResponse;
import VO.ExecutionStateResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class ReportManager {


    private static final String RUTA = "/rest/reports/executionsData";


    private UnaCloudConnection uc;

    private Gson gson;


    public ReportManager(UnaCloudConnection uc) {
        this.uc = uc;
        gson = new GsonBuilder().setExclusionStrategies(new CustomExclusionStrategy()).create();

    }


    public DeploymentExecutionResponse getRelation(String hostName) throws Exception {
        String jsonResponse = uc.getInfoFromUrl(RestVerb.GET, RUTA+"/"+hostName, null);
        //Do mapping to json with gson library
        Type collectionType = new TypeToken<Collection<DeploymentResponse>>() {
        }.getType();
        return gson.fromJson(jsonResponse,DeploymentExecutionResponse.class);
    }


}
