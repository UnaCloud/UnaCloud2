package uniandes.unacloud.web.domain

import uniandes.unacloud.common.utils.CalendarUtils;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Entity to represent a group of instances requested by user to be executed on UnaCloud infrastructure.
 * Deployment has a cluster, a list of image in cluster, a time range and a status
 * @author CesarF
 *
 */
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
	
	/**
	 * Stop time is not defined possibly
	 * cluster could be deleted but deployment history not
	 */
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
			for(Execution vm in image.getActiveExecutions()){				
				Date currentDate = new Date()
				if(vm.status ==ExecutionStateEnum.QUEUED){
					if((currentDate.getTime()-vm.getLastStateTime().getTime())>vm.status.getTime()){
						vm.putAt("status", ExecutionStateEnum.FAILED)
						vm.putAt("message",'Task failed')
					}
				}else if(vm.status ==ExecutionStateEnum.CONFIGURING){
					if((currentDate.getTime()-vm.getLastStateTime().getTime())>vm.status.getTime()){
						vm.putAt("status", ExecutionStateEnum.FAILED)
						vm.putAt("message",'Request timeout')
					}					
				}else if(vm.status ==ExecutionStateEnum.DEPLOYING){
					if(vm.stopTime==null){
						vm.putAt("status", ExecutionStateEnum.FAILED)
						vm.putAt("message",'Deploying error')
					}else if((currentDate.getTime()-vm.getLastStateTime().getTime())>vm.status.getTime()){
						vm.putAt("status", ExecutionStateEnum.FAILED)
						vm.putAt("message",'Task failed')
					}						
				}else if(vm.status ==ExecutionStateEnum.DEPLOYED){
					if(vm.stopTime==null){
						vm.putAt("stopTime", new Date())
						vm.putAt("status", ExecutionStateEnum.FAILED)
						vm.putAt("message",'Deploying error')
					}else if(vm.stopTime.before(currentDate)){
						vm.finishExecution()
					}
				}else if(vm.status ==ExecutionStateEnum.RECONNECTING){
					if(vm.lastReport&&((currentDate.getTime()-vm.lastReport.getTime())<CalendarUtils.MINUTE*4)){//if last message was before 4 minutes
						vm.putAt("status", ExecutionStateEnum.DEPLOYED)
						vm.putAt("message",'Reconnecting on '+vm.getLastStateTime())
					}else if((currentDate.getTime()-vm.getLastStateTime().getTime())>vm.status.getTime()){
						vm.putAt("status", ExecutionStateEnum.FAILED)
						vm.putAt("message",'Connection lost')
					}
				}else if(vm.status ==ExecutionStateEnum.REQUEST_COPY){
					if((currentDate.getTime()-vm.getLastStateTime().getTime())>vm.status.getTime()){
						vm.putAt("status", ExecutionStateEnum.DEPLOYED)						
						if(vm.message.contains("Copy request to image ")){
							try{
								Long imageId = Long.parseLong(vm.message.replace("Copy request to image ", ""))
								Image.get(imageId).delete()
							}catch(Exception e){
								e.printStackTrace()
							}							
						}
						vm.putAt("message",'Image copy request failed')
					}
				}else if(vm.status ==ExecutionStateEnum.COPYING){
					if((currentDate.getTime()-vm.getLastStateTime().getTime())>vm.status.getTime()){
						vm.putAt("status", ExecutionStateEnum.FAILED)						
						if(vm.message.contains("Copy request to image ")){
							try{
								Long imageId = Long.parseLong(vm.message.replace("Copy request to image ", ""))
								Image.get(imageId).putAt("state", ImageEnum.UNAVAILABLE)
							}catch(Exception e){
								e.printStackTrace()
							}							
						}
						vm.putAt("message",'Image copy request failed')
					}
				}else if(vm.status ==ExecutionStateEnum.FINISHING){
					if((currentDate.getTime()-vm.getLastStateTime().getTime())>vm.status.getTime()){
						vm.finishExecution()
					}
				}else if(vm.status ==ExecutionStateEnum.FAILED){
					if(vm.stopTime!=null&&vm.stopTime.before(currentDate)){
						vm.finishExecution()
					}
				}
			}
		}
	}
	
	/**
	 * Verifies and refresh the deployment status
	 * @return After refreshing update process return true if the deployment is active or false in case of not.
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
	 * Returns database id for entity
	 * @return Long id
	 */
	def Long getDatabaseId(){
		return id;
	}
	
	/**
	 * Deletes all history for deployment and images.
	 * It is necessary this method due to belong property among classes
	 */
	def deleteDeploy(){
		for(DeployedImage image: images){
			image.executions.each{
				def exec = it
				ExecutionRequest.where{execution==exec}.list().each {
					it.delete();
				}
				exec.delete();
			}
			image.delete()
		}
		this.delete()
	}
}
