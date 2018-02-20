package Connection;

import VO.Deployment;
import VO.ObjectId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
