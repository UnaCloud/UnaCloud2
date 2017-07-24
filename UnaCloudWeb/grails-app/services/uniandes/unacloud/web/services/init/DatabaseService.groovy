package uniandes.unacloud.web.services.init

import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;

import uniandes.unacloud.common.enums.ExecutionStateEnum;

import grails.transaction.Transactional
import groovy.sql.Sql

/**
 * This service is only for initial process.
 * Service creates triggers, store procedures and events in database
 * This class must be a groovy service due to it uses hibernate connection.
 * 
 * @author CesarF
 *
 */
@Transactional
class DatabaseService {

    //-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Datasource representation
	 */	
	def dataSource
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Executes SQL process in database to create events, triggers and store procedures
	 */
	def initDatabase(){
		def sql = new Sql(dataSource)
		//Creates event to control executions
		try {
			sql.execute ('DROP PROCEDURE IF EXISTS sp_check_vm')
			sql.execute ('CREATE PROCEDURE sp_check_vm() '
						+ 'BEGIN '
							+ 'SELECT @deployedID := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.DEPLOYED + '\';'
							+ 'SELECT @reconnectID := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.RECONNECTING + '\';'
								//Update executions in reconnecting state to deployed in case last report is greater than reconnecting time
							+ 'UPDATE execution exe JOIN execution_state exest ON exe.state_id = exest.id JOIN execution_history exehis ON exe.id = exehis.execution_id '
								+ 'SET exe.state_id = exest.next_id, exe.message = \'Reconnection succesful\', exe.last_report = CURRENT_TIMESTAMP '
								+ 'WHERE exe.last_report > exehis.change_time '
								+ 'AND exe.state_id = @reconnectID AND exehis.state_id = @reconnectID AND exe.id > 0; '
								//Update executions in deployed state to finishing in case current time exceeds stop time
							+ 'UPDATE execution exe JOIN execution_state exest ON exe.state_id = exest.id '
								+ 'SET exe.state_id = exest.next_id, exe.message = \'Finishing execution\', exe.last_report = CURRENT_TIMESTAMP '
								+ 'WHERE exe.stop_time < CURRENT_TIMESTAMP '
								+ 'AND exe.state_id = @deployedID AND exe.id > 0; '
								//Update all executions (except failed or finished) to control state if control time has been exceeded
							+ 'UPDATE execution exe JOIN execution_state exest ON exe.state_id = exest.id '
								+ 'SET exe.state_id = exest.next_control_id, exe.message = exest.message, exe.last_report = CURRENT_TIMESTAMP '
								+ 'WHERE TIMESTAMPDIFF(SECOND, exe.last_report, CURRENT_TIMESTAMP) > exest.control_time/1000 '
								+ 'AND exest.next_control_id IS NOT NULL AND exe.id > 0; '
						+ 'END')
			sql.execute ('CREATE EVENT if not exists update_vms ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP DO CALL sp_check_vm')
		} catch(Exception e) {
			print 'Error creating control execution event'
		}
		
		try {
			sql.execute 'CREATE TRIGGER '
						+ 'create_request_events AFTER INSERT ON execution '
							+ 'FOR EACH ROW BEGIN '
								+ 'INSERT INTO execution_history (state_id, change_time, execution_id, version, message) VALUES (NEW.state_id, NEW.last_report, NEW.id, 1, NEW.message); '
							+ 'END;'
		} catch(Exception e) {
			print 'create_request_events is already created'
		}                 
		try {
			sql.execute 'CREATE TRIGGER '
						+ 'save_request_events AFTER UPDATE ON execution '
							+ 'FOR EACH ROW BEGIN '
								+ 'IF NEW.state_id <> OLD.state_id THEN '
									+ 'INSERT INTO execution_history (state_id, change_time, execution_id, version, message) VALUES (NEW.state_id, NEW.last_report, NEW.id, 1, NEW.message); '
									
								+ 'END IF; '
							+ 'END'
		} catch(Exception e) {
			print 'save_request_events is already created'
		}
		try {
			sql.execute ('DROP PROCEDURE IF EXISTS sp_check_pm')
			sql.execute ('CREATE PROCEDURE sp_check_pm() BEGIN '
							+ 'UPDATE physical_machine SET state = \'' + PhysicalMachineStateEnum.OFF.name() + '\' , with_user = false where CURRENT_TIMESTAMP > DATE_ADD(last_report, INTERVAL 4 MINUTE) AND state = \'' + PhysicalMachineStateEnum.ON.name() + '\' AND id >0; '
						+ 'END')
			//sql.execute ('DROP EVENT update_pms');
			sql.execute ('CREATE EVENT if not exists update_pms ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP DO CALL sp_check_pm')
		} catch(Exception e) {
			println e.message
		}
		//sql.execute 'update physical_machine set state = \'OFF\', with_user=0;'	
		try {
			/*
			 * Events are run by the scheduler, which is not started by default. Using SHOW PROCESSLIST is possible to check whether it is started. If not, run the command
			 * http://stackoverflow.com/questions/16767923/mysql-event-not-working
			 */
			sql.execute ('SET GLOBAL event_scheduler = ON')
		} catch(Exception e) {
			println e.message
		}
		sql.close();		
	}

}
