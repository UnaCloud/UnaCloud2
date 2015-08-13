/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package unacloudws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import unacloudws.requests.VirtualClusterRequest;
import unacloudws.requests.VirtualImageRequest;
import unacloudws.responses.ClusterWS;
import unacloudws.responses.DeploymentWS;
import unacloudws.responses.ImageWS;
import unacloudws.responses.VirtualMachineExecutionWS;
import unacloudws.responses.VirtualMachineStatusEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class UnaCloudOperations {
	static ObjectMapper mapper=new ObjectMapper();
	String username;
    String apiKey;
    public UnaCloudOperations(String username, String password) {
        this.username = username;
        this.apiKey = password;
    }
    
    
    public List<VirtualMachineExecutionWS> getDeploymentInfo(long deploymentId) {
    	
    	try {
			String string=(prepareRequest("getDeploymentInfo").queryParam("depId",mapper.writeValueAsString(deploymentId)).post(String.class));
			JsonNode response= mapper.readTree(string);
			if (response.has("message")){
				System.out.println(response.get("message").asText());
				return null;
			}
			ArrayList<VirtualMachineExecutionWS> vmList= new ArrayList<VirtualMachineExecutionWS>();
			Iterator<JsonNode> it=response.iterator();
			while (it.hasNext()){
				JsonNode vm= it.next();
				VirtualMachineExecutionWS vme= new VirtualMachineExecutionWS(vm.get("belongs_to_image").asLong(), vm.get("ip").asText(), VirtualMachineStatusEnum.valueOf(vm.get("status").asText()),vm.get("message").asText(), vm.get("id").asInt(), new Date(vm.get("stop_time").asLong()), vm.get("hostname").asText());
				vmList.add(vme);
			}
			return vmList;
    	} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    
    public String addInstances(int imageId, int instances,long time){
    	try {
    		
			return prepareRequest("addInstances").queryParam("imageId",mapper.writeValueAsString(imageId)).queryParam("instances",mapper.writeValueAsString(instances)).queryParam("time",mapper.writeValueAsString(time)).post(String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    public DeploymentWS startVirtualCluster(VirtualClusterRequest clusterRequest) {
    	JsonNode cluster = mapper.createObjectNode();
    	ObjectNode clusterON=((ObjectNode) cluster);
    	clusterON.put("clusterId", clusterRequest.getClusterId());
    	clusterON.put("execTime", clusterRequest.getTime());
    	clusterON.put("images", mapper.valueToTree(clusterRequest.getVms()));
    	try {
			JsonNode response = mapper.readTree(prepareRequest("startCluster").queryParam("cluster",mapper.writeValueAsString(cluster)).post(String.class));
			long id=response.get("id").asLong();
			JsonNode clus=response.get("cluster");
			ClusterWS cl=new ClusterWS(clus.get("id").asLong(),"");
			return new DeploymentWS(id, cl);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public String startHeterogeneousVirtualCluster(int clusterId, long time,VirtualImageRequest...vms) {
    	JsonNode cluster = mapper.createObjectNode();
    	ObjectNode clusterON=((ObjectNode) cluster);
    	clusterON.put("clusterId", clusterId);
    	clusterON.put("execTime", time);
    	clusterON.put("images", mapper.valueToTree(vms));
    	try {
			return prepareRequest("startHeterogeneousCluster").queryParam("cluster",mapper.writeValueAsString(cluster)).post(String.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
    }
    public String stopVirtualMachine(long virtualMachineExId) {
    	try {
			return prepareRequest("stopVirtualMachine").queryParam("machineId",mapper.writeValueAsString(virtualMachineExId)).post(String.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public String stopDeployment(long depId) {
    	try {
			return prepareRequest("stopDeployment").queryParam("depId",mapper.writeValueAsString(depId)).post(String.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public List<DeploymentWS> getActiveDeployments(){
    	try{
    	String string=(prepareRequest("getActiveDeployments").post(String.class));
		System.out.println(string);
		JsonNode response= mapper.readTree(string);
		if (response.has("message")){
			System.out.println(response.get("message").asText());
			return null;
		}
		List<DeploymentWS> depList= new ArrayList<DeploymentWS>();
		Iterator<JsonNode> it=response.iterator();
		while(it.hasNext()){
			JsonNode data= it.next();
			ClusterWS cluster= new ClusterWS(data.get("cluster_id").asLong(), data.get("cluster_name").asText());
			DeploymentWS dep= new DeploymentWS(data.get("deployment_id").asLong(),cluster);
			depList.add(dep);
    	}
		return depList;
		}
    	catch(Exception e){
    		e.printStackTrace();
        	return null;
    	}
    }
    public List<ClusterWS> getClusterList() {
		try {
			JsonNode response = mapper.readTree(prepareRequest("getClusterList").post(String.class));
			if (response.has("message")){
				System.out.println(response.get("message").asText());
				return null;
			}
			System.out.println(response); 
			List<ClusterWS> list= new ArrayList<ClusterWS>();
	    	Iterator<JsonNode> iterator= response.iterator();
	    	while(iterator.hasNext()){
	    		ArrayList<ImageWS> imagesList= new ArrayList<ImageWS>();
	    		JsonNode clusterProperties= iterator.next();
	    		Iterator<JsonNode> imageIt= clusterProperties.get("images").iterator();
	    		while(imageIt.hasNext()){
	    			JsonNode imageProps=imageIt.next();
	    			System.out.println(imageProps);
	    			ImageWS image= new ImageWS(imageProps.get("image_id").asLong(),imageProps.get("name").asText());
	    			imagesList.add(image);
	    		}
	    		ClusterWS cluster= new ClusterWS(clusterProperties.get("cluster_id").asLong(),clusterProperties.get("name").asText(),imagesList);
	    		list.add(cluster);
	    	}
	    	return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
		
    	
    }

    public static Integer getTotalUnaCloudResources(int machineDisk, int machineCores, int machineRam) {
        /*UnaCloudWSService service = new UnaCloudWSService();
        UnaCloudWS port = service.getUnaCloudWSPort();
        return port.getTotalUnaCloudResources(machineDisk, machineCores, machineRam);*/
    	return null;
    }

    public static Integer getAvailableUnaCloudResources(int machineDisk, int machineCores, int machineRam) {
        /*UnaCloudWSService service = new UnaCloudWSService();
        UnaCloudWS port = service.getUnaCloudWSPort();
        return port.getAvailableUnaCloudResources(machineDisk, machineCores, machineRam);*/
    	return null;
    }

    public Integer getTotalVirtualMachines(int machineDisk, int machineCores, int machineRam, int templateCode) {
        /*UnaCloudWSService service = new UnaCloudWSService();
        UnaCloudWS port = service.getUnaCloudWSPort();
        return port.getTotalVirtualMachines(machineDisk, machineCores, machineRam, templateCode, username, apiKey);*/
    	return null;
    }

    public Integer getBusyUnaCloudResources(int machineDisk, int machineCores, int machineRam) {
        /*UnaCloudWSService service = new UnaCloudWSService();
        UnaCloudWS port = service.getUnaCloudWSPort();
        return port.getBusyUnaCloudResources(machineDisk, machineCores, machineRam);*/
    	return null;
    }
    public String changeAllocationPolicy(String newPolicy){
    	return prepareRequest("changeAllocationPolicy").queryParam("allocationPolicy", newPolicy).post(String.class);
    }
    
    private WebResource prepareRequest(String serviceName){
    	DefaultClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		WebResource resource = client.resource("http://unacloud.uniandes.edu.co/Unacloud2/WebServices");
		//WebResource resource = client.resource("http://157.253.236.163:8080/Unacloud2/WebServices");
		return resource.path(serviceName).queryParam("login",username).queryParam("apiKey",apiKey);
    }
}
