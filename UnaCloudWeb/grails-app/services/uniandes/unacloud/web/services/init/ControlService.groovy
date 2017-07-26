package uniandes.unacloud.web.services.init

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat

import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.utils.CalendarUtils;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.web.domain.Execution
import uniandes.unacloud.web.domain.ExecutionHistory;
import uniandes.unacloud.web.domain.ExecutionState;
import uniandes.unacloud.web.domain.Image;
import uniandes.unacloud.web.services.DeploymentService;
import grails.transaction.Transactional
import groovy.sql.Sql

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
	 * Datasource representation
	 */
	def dataSource
	
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
		def sql = new Sql(dataSource)
		def executions = deploymentService.getActiveExecutions();
		def dbTime = sql.rows("SELECT CURRENT_TIMESTAMP")		
		println dbTime
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)		
		Date current = format.parse(dbTime)
		
		executions.each {
			
			Execution exe = it
			
			if (exe.state == ExecutionStateEnum.RECONNECTING) {
				ExecutionHistory history = exe.getHistoryStatus(ExecutionStateEnum.RECONNECTING)
				if (history != null && exe.lastReport.after(history.changeTime)) {
					exe.goNext("Reconnection succesful")
					exe.save()
				}
			}
			else if (exe.state == ExecutionStateEnum.DEPLOYED) {
				if (exe.stopTime.before(current) ) {
					exe.goNext("Finishing execution")
					exe.save()
				}
			}
			
			if (exe.isControlExceeded(current)) {
				if (exe.state == ExecutionStateEnum.REQUEST_COPY) {
					Image.get(exe.copyTo).delete()
					exe.copyTo = 0;
				}
				else if (exe.state == ExecutionStateEnum.COPYING) { //es necesario dejarla inhabilitada para que sea eliminada posteriormente. Validar este punto.
					Image.get(exe.copyTo).putAt("state", ImageEnum.UNAVAILABLE)
					exe.copyTo = 0;
					exe.stopTime = current
				}			
				else if (exe.state == ExecutionStateEnum.RECONNECTING) 
					exe.stopTime = current
				
				if (exe.state != ExecutionStateEnum.DEPLOYED && exe.state != ExecutionStateEnum.REQUEST_COPY)
					exe.breakFreeInterfaces()
				exe.goNextControl(current)
				exe.save()
			}		
		}		
	}	
}
