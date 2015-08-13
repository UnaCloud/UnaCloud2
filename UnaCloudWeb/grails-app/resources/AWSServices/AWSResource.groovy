package AWSServices

import grails.converters.XML
import groovy.xml.MarkupBuilder

import javax.ws.rs.*
import javax.ws.rs.core.*;

import unacloud2.User;
import unacloud2.VirtualMachineImageService;
import back.services.AWSAPIService;

@Path('/api/AWS')
class AWSResource {
	
	AWSAPIService AWSAPIService
	VirtualMachineImageService virtualMachineImageService
	
    @POST
	@Consumes('application/x-www-form-urlencoded')
	@Produces('application/xml')
    String getAWSRepresentation(MultivaluedMap<String, String> formParams, @FormParam("unused") String unused) {
		println formParams
		LinkedList action= formParams.get('Action')
		if(action==null){
			println 'no action param received'
			AWSAPIService.sendAWSErrorResponse("InvalidAction", "The action urn:Post is not valid for this web service.")
		}
		else{
			switch(action.getFirst()){
				case "DescribeImages": 
					return AWSAPIService.describeImages(formParams)
				case "DescribeInstances":
					return AWSAPIService.describeInstances(formParams)
				case "DescribeRegions":
					return AWSAPIService.describeRegions(formParams)
				case "TerminateInstances":
					return AWSAPIService.terminateInstances(formParams)
				case "RebootInstances":
					return AWSAPIService.rebootInstances(formParams)
				case "RunInstances":
					return AWSAPIService.runInstances(formParams)
				
				default: 
				AWSAPIService.sendAWSErrorResponse("InvalidAction", "The action "+action+" is not valid for this web service.")
			}
			
		}	
    }
	
	
}
