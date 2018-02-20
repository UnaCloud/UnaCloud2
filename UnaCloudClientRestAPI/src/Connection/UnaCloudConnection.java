package Connection;

import VO.Deployment;
import java.util.List;

/**
 * Class that represents the connection with UnaCloud
 */
public class UnaCloudConnection {
    //User key
    private String userKey;
    //Base url
    private String baseUrl;
    /**
     * UnaCloudConnection constructor
     * @param userKey The user key
     * @param baseUrl The base url
     */
    public UnaCloudConnection(String userKey, String baseUrl)
    {
        this.userKey=userKey;
        this.baseUrl = baseUrl;
    }

    public String getUserKey()
    {
        return userKey;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public static void main(String[] args) throws Exception {


        UnaCloudConnection uc = new UnaCloudConnection("E72EOKECIA79DZO89ME7M5NWLZAF5MXI","http://localhost:8080");
        DeploymentConnection dep= new DeploymentConnection(uc);
        List<Deployment> list=dep.getDeployments();
        for(Deployment d:list)
            System.out.println(d.id+" "+d.duration+" "+d.cluster.id);



    }

}
