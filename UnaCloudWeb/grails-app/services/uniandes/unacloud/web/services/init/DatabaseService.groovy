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
		try {
			sql.execute 'CREATE TRIGGER create_request_events AFTER INSERT ON execution FOR EACH ROW BEGIN INSERT INTO execution_request (status, request_time, execution_id, version, message) VALUES (NEW.status, CURRENT_TIMESTAMP, NEW.id, 1, NEW.message); END;'
		} catch(Exception e) {
			print 'create_request_events is already created'
		}
		try {
			sql.execute 'CREATE TRIGGER save_request_events AFTER UPDATE ON execution FOR EACH ROW BEGIN IF NEW.status <> OLD.status THEN INSERT INTO execution_request (status, request_time, execution_id, version, message) VALUES (NEW.status, CURRENT_TIMESTAMP, NEW.id, 1, NEW.message); UPDATE execution set last_update = CURRENT_TIMESTAMP WHERE id = NEW.id;END IF; END'
		} catch(Exception e) {
			print 'save_request_events is already created'
		}
		try {
			sql.execute ('DROP PROCEDURE IF EXISTS sp_check_pm')
			sql.execute ('CREATE PROCEDURE sp_check_pm() BEGIN UPDATE physical_machine SET state = \'' + PhysicalMachineStateEnum.OFF.name() + '\' , with_user = false where CURRENT_TIMESTAMP > DATE_ADD(last_report, INTERVAL 4 MINUTE) AND state = \'' + PhysicalMachineStateEnum.ON.name() + '\' AND id >0; END')
			//sql.execute ('DROP EVENT update_pms');
			sql.execute ('CREATE EVENT if not exists update_pms ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP DO CALL sp_check_pm')
		} catch(Exception e) {
			println e.message
		}
		try{
			sql.execute ('DROP PROCEDURE IF EXISTS sp_check_vm')
			sql.execute ('DROP EVENT update_vms');
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
