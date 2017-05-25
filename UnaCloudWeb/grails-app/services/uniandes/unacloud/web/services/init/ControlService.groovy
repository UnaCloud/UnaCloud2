package uniandes.unacloud.web.services.init

import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.utils.CalendarUtils;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.web.domain.Execution
import uniandes.unacloud.web.domain.Image;
import uniandes.unacloud.web.services.DeploymentService;
import grails.transaction.Transactional

/**
 * This service is responsible to control status of some entities
 * @author CesarF
 *
 */
@Transactional
class ControlService {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of deployment services
	 */
	
	DeploymentService deploymentService
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

	/**
	 * Changes and control status in all active executions based in status graph
	 */
	def validateExecutionStates() {
		
		def executions = deploymentService.getActiveExecutions();
		
		executions.each {
			Date currentDate = new Date()
			Execution exe = it
			if(exe.status == ExecutionStateEnum.QUEUED) {
				if(exe.isAboveStateTime(currentDate)) {
					exe.putAt("status", ExecutionStateEnum.FAILED)
					exe.putAt("message",'Task failed: too much time in queue')
				}
			}
			else if(exe.status == ExecutionStateEnum.CONFIGURING) {
				if(exe.isAboveStateTime(currentDate)) {
					exe.putAt("status", ExecutionStateEnum.FAILED)
					exe.putAt("message",'Configuring process failed in agent')
				}
			}
			else if(exe.status == ExecutionStateEnum.DEPLOYING) {
				if(exe.stopTime == null) {
					exe.putAt("status", ExecutionStateEnum.FAILED)
					exe.putAt("message",'Deploying error')
				}
				else if(exe.isAboveStateTime(currentDate)) {
					exe.putAt("status", ExecutionStateEnum.FAILED)
					exe.putAt("message",'Task failed: too much time in deploying')
				}
			}
			else if(exe.status == ExecutionStateEnum.DEPLOYED) {
				if(exe.stopTime == null) {
					exe.putAt("stopTime", new Date())
					exe.putAt("status", ExecutionStateEnum.FAILED)
					exe.putAt("message",'Deploying error')
				}
				else if(exe.stopTime.before(currentDate)) {
					exe.finishExecution()
				}
				else if(exe.lastReport != null && currentDate.getTime() - exe.lastReport.getTime() > CalendarUtils.MINUTE * 4) {
					exe.putAt("status", ExecutionStateEnum.RECONNECTING);
					exe.putAt("message","Execution has not been reported for a few minutes");					
				}
			}
			else if(exe.status == ExecutionStateEnum.RECONNECTING) {
				//if last message was before 4 minutes, it means that execution is alive but after 4 is still reconnecting
				if(exe.lastReport && ((currentDate.getTime() - exe.lastReport.getTime()) < CalendarUtils.MINUTE * 4)) {
					exe.putAt("status", ExecutionStateEnum.DEPLOYED)
					exe.putAt("message",'Reconnected on '+exe.lastReport.getTime())
				}
				else if(exe.isAboveStateTime(currentDate)) {
					exe.putAt("status", ExecutionStateEnum.FAILED)
					exe.putAt("message",'Connection lost')
				}
			}
			else if(exe.status ==ExecutionStateEnum.REQUEST_COPY) {
				if(exe.isAboveStateTime(currentDate)) {
					exe.putAt("status", ExecutionStateEnum.DEPLOYED)
					if(exe.message.contains("Copy request to image ")) {
						try{
							Long imageId = Long.parseLong(exe.message.replace("Copy request to image ", ""))
							Image.get(imageId).delete()
						} catch(Exception e) {
							e.printStackTrace()
						}
					}
					exe.putAt("message",'Image copy request failed')
				}
			}
			else if(exe.status ==ExecutionStateEnum.COPYING) {
				if(exe.isAboveStateTime(currentDate)) {
					exe.putAt("status", ExecutionStateEnum.FAILED)
					if(exe.message.contains("Copy request to image ")) {
						try{
							Long imageId = Long.parseLong(exe.message.replace("Copy request to image ", ""))
							Image.get(imageId).putAt("state", ImageEnum.UNAVAILABLE)
						} catch(Exception e) {
							e.printStackTrace()
						}
					}
					exe.putAt("message",'Image copy request failed')
				}
			}
			else if(exe.status ==ExecutionStateEnum.FINISHING) {
				if(exe.isAboveStateTime(currentDate)) {
					exe.finishExecution()
				}
			}
			else if(exe.status ==ExecutionStateEnum.FAILED) {
				if(exe.stopTime!=null&&exe.stopTime.before(currentDate)) {
					exe.finishExecution()
				}
			}
		}		
	}	
}
