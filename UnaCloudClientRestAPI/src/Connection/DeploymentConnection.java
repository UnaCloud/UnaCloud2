package Connection;

import VO.Deployment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * Class used to manipulate deployment connections
 */
public class DeploymentConnection {
    //Attribute for UnaCloudConnection
    private UnaCloudConnection uc;
    //Attribute for gson connection for parsing
    private Gson gson;

    /**
     * Creates new deployment connection with the given UnaCloudConnection and a gson with desired json mapping criteria
     * @param uc
     */
    public DeploymentConnection(UnaCloudConnection uc)
    {
        this.uc=uc;
        gson = new GsonBuilder().setExclusionStrategies(new CustomExclusionStrategy()).create();

    }

    /**
     * Gets all the deployments of the user found in UnaCloudConnection
     * @return list of active user deployments
     * @throws Exception If there is any issue during the http request
     */
    public List<Deployment> getDeployments() throws Exception
    {

            //Base connection with RestAPI
            String url = uc.getBaseUrl()+"/UnaCloudWeb/rest/deployment";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("key", uc.getUserKey());

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);


            //Get response content
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());



            //Do mapping to json with gson library
            Type collectionType = new TypeToken<Collection<Deployment>>(){}.getType();
            return gson.fromJson(response.toString(), collectionType);


    }
}
