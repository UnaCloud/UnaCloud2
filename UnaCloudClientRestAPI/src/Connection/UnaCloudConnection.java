package Connection;

import VO.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
    //---------------
    //Getter and setter
    //---------------
    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    //----------------
    //----------------
    /**
     * Universal method for doing http request without any header params besides the user key and the content-type and acceptance.<br>
     * @param verb Http verb (GET,POST,PUT,DELETE)
     * @param givenUrl Url of the resource that is going to be accesed.
     * @param jsonBody Body in json format. Null in case it is not necessary.
     * @return Response as a string.
     * @throws Exception If any IOException is generated during the information retrieval or any HttpException happens.
     */
    public String getInfoFromUrl(RestVerb verb, String givenUrl, JSONObject jsonBody) throws Exception
    {
        //Base connection with RestAPI
        String url = baseUrl+givenUrl;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // GET
        con.setRequestMethod(verb.toString());

        //add request header
        con.setRequestProperty("key", userKey);

        if(jsonBody!=null)
        {
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json; charset=UTF-8");

            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(jsonBody.toString());
            wr.flush();
            wr.close();
        }

        int responseCode = con.getResponseCode();
        System.out.println("\nSending "+ verb+" request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in=null;

        boolean isError=false;
        if(responseCode==200)
            //Get response content
             in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        else
        {
            //Get error content
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            isError=true;
        }

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //Throw exception if there is an error
        if(isError)
        {
            throw new Exception(response.toString());
        }

        return response.toString();
    }
    /**
     * Test for main.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        UnaCloudConnection uc = new UnaCloudConnection("E72EOKECIA79DZO89ME7M5NWLZAF5MXI","http://localhost:8080");
        DeploymentManager dep= new DeploymentManager(uc);
        //Post deployment with params
        DeploymentRequest deploymentRequest=new DeploymentRequest(2,2);
        deploymentRequest.addNode(13,1,2,"MyHost2",false);
        double deploymentId=dep.deployWithParams(deploymentRequest);
        System.out.println("ID DEPLOY"+deploymentId);

        //Get the current deployment
        DeploymentResponse deploy=dep.getDeployment((int)deploymentId);
        System.out.println(deploy.getStatus().getName()+"");

        double time=System.nanoTime();
        //Assume we have executions
        System.out.println("Get executions");
        boolean todoEstaDetenido=false;
        int state=0;
        while(!todoEstaDetenido)
        {
            Thread.sleep(60000);
            todoEstaDetenido=true;
            for(ObjectId<Integer> id:deploy.getImages())
            {
                for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                {
                    state=exec.getState().getId();
                    System.out.println("EXEC "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                    if(state!=DeploymentManager.DEPLOYED && state!=DeploymentManager.FAILED)
                    {
                        todoEstaDetenido=false;
                        break;
                    }
                }
            }
        }

        System.out.println("TIME "+(System.nanoTime()-time));

        ArrayList<Integer> machines=new ArrayList<>();
        System.out.println("Stop");
        DeploymentStopRequest deploymentStopRequest=new DeploymentStopRequest();
        //Cycle through all executions of the deployment and add them to the list for stopping
        for(ObjectId<Integer> id:deploy.getImages())
        {
            for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
            {
                System.out.println("ADD "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                deploymentStopRequest.addExecution(exec.getId());
                machines.add(exec.getExecutionNode().getId());
            }
        }

        //Stop executions
        dep.stopExecutions(deploymentStopRequest);

        //Clean the cache of the given machines or of given numbers.
        System.out.println("Cache");

        LaboratoryManager lab=new LaboratoryManager(uc);
        LaboratoryUpdateRequest laboratoryUpdateRequest=new LaboratoryUpdateRequest(1, TaskManagerState.CACHE);
        for(Integer i:machines)
        {
            System.out.println("MACHINE "+i);
            laboratoryUpdateRequest.addMachine(i);
        }
        lab.cleanCache(laboratoryUpdateRequest);

    }

}
