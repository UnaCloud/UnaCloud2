package unacloud

//import back.services.ExternalCloudCallerService;
import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.utils.CalendarUtils;

class Deployment {
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Deployed cluster representation 
	 */
	Cluster cluster
	
	/**
	 * start time of the deployment
	 */
	Date startTime
	
	/**
	 * stop time of the deployment
	 */
	Date stopTime
	
	/**
	 * represent status of the deployment (ACTIVE, FINISHED)
	 */
	DeploymentStateEnum status
	
	/**
	 * list of deployed images present in the deployment
	 */
	static hasMany = [images: DeployedImage]
	
	/**
	 * Owner
	 */
	static belongsTo = [user: User]
	
	static constraints = {	
		stopTime nullable:true 
		cluster nullable:true
    }
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Refresh the deployment status verifying all nodes
	 */
	def updateState(){
		for(image in images) {
			for(VirtualMachineExecution vm in image.getActiveExecutions()){				
				Date currentDate = new Date()
				if(vm.status ==VirtualMachineExecutionStateEnum.QUEQUED){
					if(currentDate.getTime()-vm.getLastStateTime().getTime()>vm.status.getTime()){
						vm.putAt("status", VirtualMachineExecutionStateEnum.FAILED)
						vm.putAt("message",'Task failed')
					}
				}else if(vm.status ==VirtualMachineExecutionStateEnum.CONFIGURING){
					if(currentDate.getTime()-vm.getLastStateTime().getTime()>vm.status.getTime()){
						vm.putAt("status", VirtualMachineExecutionStateEnum.FAILED)
						vm.putAt("message",'Request timeout')
					}					
				}else if(vm.status ==VirtualMachineExecutionStateEnum.DEPLOYING){
					if(vm.stopTime==null){
						vm.putAt("status", VirtualMachineExecutionStateEnum.FAILED)
						vm.putAt("message",'Deploying error')
					}else if(currentDate.getTime()-vm.getLastStateTime().getTime()>vm.status.getTime()){
						vm.putAt("status", VirtualMachineExecutionStateEnum.FAILED)
						vm.putAt("message",'Task failed')
					}						
				}else if(vm.status ==VirtualMachineExecutionStateEnum.DEPLOYED){
					if(vm.stopTime==null){
						vm.putAt("stopTime", new Date())
						vm.putAt("status", VirtualMachineExecutionStateEnum.FAILED)
						vm.putAt("message",'Deploying error')
					}else if(vm.stopTime.before(currentDate)){
						vm.finishExecution()
					}
				}else if(vm.status ==VirtualMachineExecutionStateEnum.RECONNECTING){
					if(currentDate.getTime()-vm.getLastStateTime().getTime()>vm.status.getTime()){
						vm.putAt("status", VirtualMachineExecutionStateEnum.FAILED)
						vm.putAt("message",'Connection lost')
					}	
				}else if(vm.status ==VirtualMachineExecutionStateEnum.REQUEST_COPY){
					if(currentDate.getTime()-vm.getLastStateTime().getTime()>vm.status.getTime()){
						vm.putAt("status", VirtualMachineExecutionStateEnum.DEPLOYED)
						vm.putAt("message",'Copy image request failed')
						if(vm.message.contains("Copy request to image ")){
							try{
								Long imageId = Long.parseLong(vm.message.replace("Copy request to image ", ""))
								VirtualMachineImage.get(imageId).delete()
							}catch(Exception e){
								e.printStackTrace()
							}							
						}
					}
				}else if(vm.status ==VirtualMachineExecutionStateEnum.COPYING){
					if(currentDate.getTime()-vm.getLastStateTime().getTime()>vm.status.getTime()){
						vm.putAt("status", VirtualMachineExecutionStateEnum.FAILED)
						vm.putAt("message",'Copy image failed')
						if(vm.message.contains("Copy request to image ")){
							try{
								Long imageId = Long.parseLong(vm.message.replace("Copy request to image ", ""))
								VirtualMachineImage.get(imageId).delete()
							}catch(Exception e){
								e.printStackTrace()
							}							
						}
					}
				}else if(vm.status ==VirtualMachineExecutionStateEnum.FINISHING){
					if(currentDate.getTime()-vm.getLastStateTime().getTime()>vm.status.getTime()){
						vm.finishExecution()
					}
				}else if(vm.status ==VirtualMachineExecutionStateEnum.REQUEST_FINISH){
					if(currentDate.getTime()-vm.getLastStateTime().getTime()>vm.status.getTime()){
						vm.putAt("status", VirtualMachineExecutionStateEnum.DEPLOYED)
						vm.putAt("message",'Finish execution request failed')
					}
				}
				else if(vm.status ==VirtualMachineExecutionStateEnum.FAILED){
					if(vm.stopTime!=null&&vm.stopTime.before(currentDate)){
						vm.finishExecution()
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
			for(image in images) {
				if(image.getActiveExecutions().size()>0)				
					return true
			}
		}
		return false
	}
	
	/**
	 * Returns database id
	 * @return
	 */
	def Long getDatabaseId(){
		return id;
	}
}
