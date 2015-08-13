package unacloud2

import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;

import communication.messages.vmo.VirtualMachineStartResponse.VirtualMachineState;
import back.services.ExternalCloudCallerService;
import unacloud2.enums.DeploymentStateEnum;
import unacloudEnums.VirtualMachineExecutionStateEnum;

class Deployment {
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Deployed cluster representation 
	 */
	DeployedCluster cluster
	
	/**
	 * start time of the deployment
	 */
	Date startTime
	
	/**
	 * stop time of the deployment
	 */
	Date stopTime
	
	/**
	 * present status of the deployment (ACTIVE of FINISHED)
	 */
	DeploymentStateEnum status
	
	static constraints = {	
		stopTime nullable:true 
    }
	
	//def externalCloudCallerService
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Counts all deployment active virtual machines
	 * @return number of active virtual machines in this deployment
	 */
	def getTotalActiveVMs(){
		def totalVMs =0
		cluster.images.each {
			it.virtualMachines.each {
				if(it.status!= VirtualMachineExecutionStateEnum.FINISHED)
				totalVMs++
			}
		}
		return totalVMs
	}
	
	/**
	 * Refresh the deployment status verifying all nodes
	 */
	def updateState(){
		for(image in cluster.images) {
			ArrayList<String> externalInstanceIds= new ArrayList<String>()
			for(vm in image.virtualMachines){
				if(!(vm.status ==VirtualMachineExecutionStateEnum.FINISHED)){
				if(vm.stopTime==null){
					if(vm.status.equals(VirtualMachineExecutionStateEnum.DEPLOYING))
						externalInstanceIds.add(vm.name)
				}
				else if(vm.stopTime.compareTo(new Date())<0){
					vm.status= VirtualMachineExecutionStateEnum.FINISHED
					if (vm.ip != null) vm.ip.used= false
				}
				else if((((new Date().getTime()-vm.startTime.getTime())/60000))>30 && vm.status==VirtualMachineExecutionStateEnum.DEPLOYING){
					vm.status=VirtualMachineExecutionStateEnum.FAILED
					vm.message='Request timeout'
				}
				}			}
			
			def instances
			if(externalInstanceIds.size()>0 && !(ServerVariable.findByName('EXTERNAL_COMPUTING_ACCOUNT').variable.equals('None')))
				instances = externalCloudCallerService.describeInstance(externalInstanceIds)
			for (instance in instances){
				def vm=VirtualMachineExecution.findByName(instance.instanceId)
				if (vm!= null){
					switch (instance.state.code){
						case 0: 
							vm.setStatus(VirtualMachineExecutionStateEnum.DEPLOYING)
							vm.setMessage(instance.state.name)
							break
						case 16:
							vm.setStatus(VirtualMachineExecutionStateEnum.DEPLOYED)
							def ip= new IP(ip:instance.publicIpAddress, used:true)
							ip.save(failOnError: true)
							vm.setIp(ip)
							vm.setMessage(instance.state.name)
							break
						case 32: case 64: case 80:
							vm.setStatus(VirtualMachineExecutionStateEnum.FAILED)
							vm.setMessage(instance.state.name)
							break
						case 48:
							vm.setStatus(VirtualMachineExecutionStateEnum.FINISHED)
							vm.setMessage(instance.state.name)
							break
					}
				}
			}
		}
	}
	
	/**
	 * Verifies and refresh the deployment status
	 * @return if the deployment is active or not after refreshing
	 */
	def isActive(){
		if (status==DeploymentStateEnum.ACTIVE){
		updateState()
		for(image in cluster.images) {
			for(vm in image.virtualMachines){
				if(!(vm.status ==VirtualMachineExecutionStateEnum.FINISHED))
					return true
			}
		}
		}
		return false
	}
	
	
}
