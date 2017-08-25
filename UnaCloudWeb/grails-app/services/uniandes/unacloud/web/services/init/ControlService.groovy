package uniandes.unacloud.web.services.init

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat

import uniandes.unacloud.share.enums.ExecutionStateEnum;
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
		def executions = deploymentService.getActiveExecutions();
		def sql = new Sql(dataSource)
		def dbTime = sql.rows("SELECT CURRENT_TIMESTAMP")		
		println "Rub job: " + dbTime[0].get("CURRENT_TIMESTAMP")
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)		
		Date current = format.parse(dbTime[0].get("CURRENT_TIMESTAMP").toString())
		
		executions.each {
			
			Execution exe = it
			
			//Validates if agent has send a report after lost connection
			if (exe.state.state == ExecutionStateEnum.RECONNECTING) {
				ExecutionHistory history = exe.getHistoryStatus(ExecutionStateEnum.RECONNECTING)
				if (history != null && exe.lastReport.after(history.changeTime)) {
					exe.goNext("Reconnection succesful")
					exe.save()
				}
			}
			else if (exe.state.state == ExecutionStateEnum.DEPLOYED) {
				if (exe.stopTime.before(current) ) {
					exe.goNext("Finishing execution")
					exe.save()
				}
			}
			
			if (exe.isControlExceeded(current)) {				
				exe.goNextControl()
				exe.save()
			}		
		}		
	}	
}
