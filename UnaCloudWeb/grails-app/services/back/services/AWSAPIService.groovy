package back.services

import java.awt.Image;
import java.beans.XMLDecoder;
import java.lang.reflect.Array;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import org.apache.commons.lang.RandomStringUtils;
import org.aspectj.weaver.ResolvedType.Missing;
import org.hibernate.usertype.UserVersionType;
import back.pmallocators.VirtualMachineAllocator;
import unacloud2.*;
import unacloudEnums.VirtualMachineExecutionStateEnum;
import webutils.AWSRequestException
import webutils.ImageRequestOptions;
import webutils.InternalServerException
import grails.transaction.Transactional
import groovy.xml.MarkupBuilder

@Transactional
class AWSAPIService {
	
	ClusterService clusterService
	DeploymentService deploymentService
	/**
	 * Builds a XML error with the given code and message
	 * @param code Error type
	 * @param message Message4 to be shown
	 * @return Error formated as XML
	 */
    def sendAWSErrorResponse(String code, String message){
		def writer= new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.Response {
			Errors{
				Error{
					Code(code)
					Message(message)
				}
			}
			RequestID(generateRandomId(8))
		}	
		throw new AWSRequestException(writer.toString())
	}
	
	/**
	 * Builds a XML error with the given code and message
	 * @param code Error type
	 * @param message Message4 to be shown
	 * @return Error formated as XML
	 */
	def sendInternalServerErrorResponse(String code, String message){
		def writer= new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.Response {
			Errors{
				Error{
					Code(code)
					Message(message)
				}
			}
			RequestID(generateRandomId(8))
		}
		
		throw new InternalServerException(writer.toString())
	}
	/**
	 * 
	 * @param queryParams
	 * @return
	 */
	def describeImages(Map<String,LinkedList<String>> queryParams){
		String userValidation = validateCredentials(queryParams.get("AWSAccessKeyId"))
		User u= User.get(userValidation)
		def writer= new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.DescribeImagesResponse{
			RequestId(generateRandomId(8))
			imagesSet{
			for (image in VirtualMachineImage.list()){
				if(image.getIsPublic()||u.getOrderedImages().contains(image)){
					item{
						imageId(image.getId())
						imageLocation()
						imageState()
						imageOwnerId(u.getUsername())
						isPublic(image.getIsPublic())
						architecture('x64-86')
						imageType('machine')
						kernelId(generateRandomId(8))
						ramdiskId(generateRandomId(8))
						imageOwnerAlias(u.getName())
						name(image.getName())
						description('Unacloud Image')
						rootDeviceType()
						rootDeviceName()
						blockDeviceMapping{
						}
						virtualizationType('II')
						tagSet()
						hypervisor('virtualbox')
					}
					}
				}
			}
			
		}
		println writer.toString()
		return writer.toString()
	}
	
	/**
	 * Describe Virtual Machine Executions formatted as AWS Instances. 
	 * @param queryParams request params 
	 * @return
	 */
	def describeInstances(Map<String,LinkedList<String>> queryParams){
		String userValidation = validateCredentials(queryParams.get("AWSAccessKeyId"))
		User u= User.get(userValidation)
		def deps = u.getActiveDeployments()
		def writer= new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.DescribeInstancesResponse{
			requestId(generateRandomId(8))
			reservationSet{	
			item{					
				reservationId(generateRandomId(8))
				ownerId(u.getId())
				groupSet()
				instancesSet{
					
				for(deployment in deps){
					for(image in deployment.getCluster().getImages()){
					def i=image.getImage()
					for(vm in image.getOrderedVMs()){
						item{
						instanceId(vm.id)
						imageId(image.id)
						def status = vm.getStatus()
						instanceState{
							println status
							switch (status){
							case VirtualMachineExecutionStateEnum.DEPLOYED:
								
								code('16')
								name('running')								
								break
							case VirtualMachineExecutionStateEnum.COPYING:
							case VirtualMachineExecutionStateEnum.DEPLOYING:
							case VirtualMachineExecutionStateEnum.CONFIGURING:
								
								code('0')
								name('pending')
								break 
							case VirtualMachineExecutionStateEnum.FAILED:
								code('80')
								name('stopped')
								break
							case VirtualMachineExecutionStateEnum.FINISHED:
								
								code('48')
								name('terminated')
								break
							}
						}
						privateDnsName()
						dnsName(vm.getIp().getIp())
						reason('User initiated ('+vm.getStartTime().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")+')')
						keyName("user:"+i.getUser()+"-pass:"+i.getPassword())
						amiLaunchIndex(0)
						productCodes()
						instanceType(vm.getHardwareProfile().getName())
						launchTime(vm.getStartTime().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
						placement{
						availabilityZone(vm.getExecutionNode().getLaboratory().getName())
						groupName()
						tenancy('default')
	  					}
						platform(i.getOperatingSystem().getName())
						monitoring{
							state('disabled')
						}
						subnetId(generateRandomId(8))
						vpcId("vpc-"+generateRandomId(8))
						privateIpAddress()
						ipAddress(vm.getIp().getIp())
						sourceDestCheck('true')
						groupSet{
						item{
						  groupId(generateRandomId(8))
						  groupName(generateRandomId(8))
						}
						}
						architecture('x86_64')
						rootDeviceType('ebs')
						rootDeviceName('notAvailable')
						blockDeviceMapping{}
						virtualizationType('hvm')
						clientToken(this.generateRandomId(8))
						tagSet{
						item{
						  key('name')
						  value(vm.getName())
						}
						}
						hypervisor('virtualbox')
						networkInterfaceSet{
						}
					}
					}
				}
				}
				}
			}
			}
		}
		println writer.toString()
		return writer.toString()
	
		
	}
	
	
	def describeRegions(Map<String,LinkedList<String>> queryParams){
		String userValidation = validateCredentials(queryParams.get("AWSAccessKeyId"))
		def laboratories = Laboratory.list()
		def writer= new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.DescribeRegionsResponse{
			requestId(generateRandomId())
			regionInfo{
				for (lab in laboratories){
					item{
						regionName(lab.getName())
						regionEndpoint('')
					}
				}
			}
		}
		return writer.toString()
	}
	
	def terminateInstances(Map<String,LinkedList<String>> queryParams){
		String userValidation = validateCredentials(queryParams.get("AWSAccessKeyId"))
		User u = User.get(userValidation)
		def instanceids= new TreeMap<String,String>()
		for (key in queryParams.keySet()){
			if (key.contains('InstanceId.'))
			instanceids.put(queryParams.get(key).get(0),queryParams.get(key).get(0))
		}
		def instances= new ArrayList<VirtualMachineImage>()
		if (instanceids.size()==0) sendAWSErrorResponse"MissingParameter", "The request must contain the parameter InstanceId.n"
		for(dep in u.getActiveDeployments()){
			for(i in dep.getCluster().getImages()){
				for(vm in i.getOrderedVMs()){
					if(instanceids.containsKey(vm.getId().toString())) instances.add(vm)
				}	
			}
		}
		if(instances.size()!=instanceids.size()) sendAWSErrorResponse("OperationNotPermitted", "The Instance.Id.n parameter contains invalid instance ids")
		def writer= new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.TerminateInstancesResponse{
			RequestId(generateRandomId(8))
			instancesSet{
				for(i in instances){
					if(i.getStatus()==VirtualMachineExecutionStateEnum.DEPLOYING
					||i.getStatus()==VirtualMachineExecutionStateEnum.CONFIGURING
					||i.getStatus()==VirtualMachineExecutionStateEnum.COPYING
					||i.getStatus()==VirtualMachineExecutionStateEnum.FINISHED)	
					sendAWSErrorResponse("IncorrectState", "The instance with id "+i.getId()+" cannot be stopped in the current state (0:pending)")
						
				try{
				deploymentService.stopVirtualMachineExecution(i)
				
				}
				catch(Exception e){
					sendInternalServerErrorResponse("InternalError", e.getMessage())
				}
				def status=i.getStatus()
				item{
					InstanceId(i.getId())
					currentState{
						code('32')
						name('shutting-down')
					}
					previousState{		
					switch (status){
						case VirtualMachineExecutionStateEnum.DEPLOYED:	
							code('16')
							name('running')
							break
						
						case VirtualMachineExecutionStateEnum.FAILED:
							code('48')
							name('stopped')
							break
					}
					}
				}
				}
			}
		}
		println writer.toString()
		return writer.toString()
	}
	
	def rebootInstances(Map<String,LinkedList<String>> queryParams){
		String userValidation = validateCredentials(queryParams.get("AWSAccessKeyId"))
		User u = User.get(userValidation)
		def instanceids= new TreeMap<String,String>()
		for (key in queryParams.keySet()){
			if (key.contains('InstanceId.'))
			instanceids.put(queryParams.get(key).get(0),queryParams.get(key).get(0))
			
		}
		def instances= new ArrayList<VirtualMachineImage>()
		if (instanceids.size()==0) sendAWSErrorResponse"MissingParameter", "The request must contain the parameter InstanceId.n"
		for(dep in u.getActiveDeployments()){
			for(i in dep.getCluster().getImages()){
				for(vm in i.getOrderedVMs()){
					if(instanceids.containsKey(vm.getId().toString())) instances.add(vm)
				}	
			}
		}
		println instances.size() +" "+instanceids.size()
		if(instances.size()!=instanceids.size()) sendAWSErrorResponse("InvalidInstanceID.NotFound", "The Instance.Id.n parameter contains invalid instance ids")
		def writer= new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.RebootInstancesResponse{
			requestId(generateRandomId(8))
			for(i in instances){
				if(i.getStatus()==VirtualMachineExecutionStateEnum.DEPLOYING
				||i.getStatus()==VirtualMachineExecutionStateEnum.CONFIGURING
				||i.getStatus()==VirtualMachineExecutionStateEnum.COPYING
				||i.getStatus()==VirtualMachineExecutionStateEnum.FINISHED)
				sendAWSErrorResponse("IncorrectState", "The instance with id "+i.getId()+" cannot be stopped in the current state (0:pending)")
				println deploymentService.restartVirtualMachineExecution(i)
			}
			
			'return'('true')
		}
		return writer.toString()
	}
	
	def runInstances(Map<String,LinkedList<String>> queryParams){
		String userValidation = validateCredentials(queryParams.get("AWSAccessKeyId"))
		User u = User.get(userValidation)
		def imageid
		if (queryParams.containsKey('ImageId')) imageid=queryParams.get('ImageId').get(0)
		else sendAWSErrorResponse"MissingParameter", "The request must contain the parameter ImageId"
		def i= VirtualMachineImage.get(imageid)
		if (i == null) return sendAWSErrorResponse("InvalidAMIID.NotFound", "The specified image does not exist. Check the AMI ID, and ensure that you specify the region in which the AMI is located, if it's not in the default region. This error may also occur if you specified an incorrect kernel ID when launching an instance.")
		else if((!i.getIsPublic())&&(!u.getImages().contains(i))) return sendAWSErrorResponse("InvalidAMIID.NotFound", "The specified image does not exist. Check the AMI ID, and ensure that you specify the region in which the AMI is located, if it's not in the default region. This error may also occur if you specified an incorrect kernel ID when launching an instance.")
		else
		{
			println 'image found'
			Cluster c= new Cluster(name: 'AWSCluster-'+i.getName())
			clusterService.saveCluster(i.getId(), c, u)
			def instanceTy
			if(queryParams.containsKey("InstanceType")) instanceTy= queryParams.get('InstanceType')
			else instanceTy='small'
			int instances= Integer.parseInt(queryParams.get("MaxCount").get(0))
			if(instances==null||instances==0) instances=1
			println instanceTy
			HardwareProfile hp = HardwareProfile.findByName(instanceTy)
			println hp
			if (hp == null) return sendAWSErrorResponse("InvalidInstanceAttributeValue", "The specified instance type is not valid.")
			ImageRequestOptions[] imageRequestOptions= new ImageRequestOptions[1]
			boolean[] highAvail = new boolean [1]
			highAvail[0]= false
			imageRequestOptions[0]= new ImageRequestOptions(i.getId(),hp, instances, "aws-"+i.name)
			Deployment d
			long time
			
			try{
			time= Long.parseLong(queryParams.get('AdditionalInfo').get(0))
			
			}
			catch(Exception e){
				sendAWSErrorResponse("MissingParameter","In order to use UnaCloud your request must have execution time in ms under AdditionalInfo")
			}
			try{
			d= deploymentService.deploy(c, u, time, imageRequestOptions, highAvail)
			clusterService.deleteCluster(c,u)
			}
			catch(Exception e){
				sendInternalServerErrorResponse("InternalError", e.getMessage())	
			}
			def writer= new StringWriter()
			def xml = new MarkupBuilder(writer)
			xml.RunInstancesResponse{
				requestId(generateRandomId(8))
				reservationId(generateRandomId(8))
				
					reservationId(generateRandomId(8))
					ownerId(u.getId())
					groupSet()
					instancesSet{
						
					for(image in d.getCluster().getImages()){
						i=image.getImage()
						for(vm in image.getOrderedVMs()){
							item{
							instanceId(vm.id)
							imageId(image.id)
							def status = vm.getStatus()
							instanceState{
								switch (status){
								case VirtualMachineExecutionStateEnum.DEPLOYED:
									
									code('16')
									name('running')
									break
								case VirtualMachineExecutionStateEnum.COPYING:
								case VirtualMachineExecutionStateEnum.DEPLOYING:
								case VirtualMachineExecutionStateEnum.CONFIGURING:
									
									code('0')
									name('pending')
									break
								case VirtualMachineExecutionStateEnum.FAILED:
									code('80')
									name('stopped')
									break
								case VirtualMachineExecutionStateEnum.FINISHED:
									
									code('48')
									name('terminated')
									break
								}
							}
							privateDnsName()
							dnsName(vm.getIp().getIp())
							reason('User initiated ('+vm.getStartTime().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")+')')
							keyName("user:"+i.getUser()+"-pass:"+i.getPassword())
							amiLaunchIndex(0)
							productCodes()
							instanceType(vm.getHardwareProfile().getName())
							launchTime(vm.getStartTime().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
							placement{
							availabilityZone(vm.getExecutionNode().getLaboratory().getName())
							groupName()
							tenancy('default')
							  }
							platform(i.getOperatingSystem().getName())
							monitoring{
								state('disabled')
							}
							subnetId(generateRandomId(8))
							vpcId("vpc-"+generateRandomId(8))
							privateIpAddress()
							ipAddress(vm.getIp().getIp())
							sourceDestCheck('true')
							groupSet{
							item{
							  groupId(generateRandomId(8))
							  groupName(generateRandomId(8))
							}
							}
							architecture('x86_64')
							rootDeviceType('ebs')
							rootDeviceName('notAvailable')
							blockDeviceMapping{}
							virtualizationType('hvm')
							clientToken(generateRandomId(8))
							tagSet{
							item{
							  key('name')
							  value(vm.getName())
							}
							}
							hypervisor('virtualbox')
							networkInterfaceSet{
							}
						}
						}
					
					}
					}
				
			}
			return writer.toString()
		}
	
	}
	
	String validateCredentials(String credentials){
	 	if(credentials==null)
			sendAWSErrorResponse("MissingParameter", "The request must contain the parameter AWSAccessKeyId") 
		def user= User.findByApiKey(credentials)
		if(user==null)
			sendAWSErrorResponse("AuthFailure", "Unacloud was not able to validate the provided access credentials")
		else
			return user.id
	}
	
	String generateRandomId(int chars){
		String charset = (('A'..'Z') + ('0'..'9')).join()
		return RandomStringUtils.random(chars, charset.toCharArray())
	}
}
