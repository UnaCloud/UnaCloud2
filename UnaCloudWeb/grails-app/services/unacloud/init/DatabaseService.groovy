package unacloud.init

import grails.transaction.Transactional
import groovy.sql.Sql

/**
 * This class is responsible for all 
 * @author Cesar
 *
 */
@Transactional
class DatabaseService {

    //-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Datasource representation
	 * @return
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
		try{
			sql.execute 'CREATE TRIGGER create_request_events AFTER INSERT ON virtual_machine_execution FOR EACH ROW BEGIN INSERT INTO execution_request (status, request_time, execution_id, version) VALUES (NEW.status, CURRENT_TIMESTAMP, NEW.id, 1); END;'
		}catch(Exception e){print 'create_request_events is already created'}
		try{
			sql.execute 'CREATE TRIGGER save_request_events AFTER UPDATE ON virtual_machine_execution FOR EACH ROW BEGIN IF NEW.status <> OLD.status THEN INSERT INTO execution_request (status, request_time, execution_id, version) VALUES (NEW.status, CURRENT_TIMESTAMP, NEW.id, 1); END IF; END'
		}catch(Exception e){print 'save_request_events is already created'}
		try{
			sql.execute ('DROP PROCEDURE IF EXISTS sp_check_pm')
			sql.execute ('CREATE PROCEDURE sp_check_pm() BEGIN UPDATE physical_machine SET state = \'OFF\' where CURRENT_TIMESTAMP > DATE_ADD(last_report, INTERVAL 4 MINUTE) AND state = \'ON\'; END')
			sql.execute ('CREATE EVENT if not exists update_pms ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP DO EXECUTE sp_check_pm')
		}catch(Exception e){println e.message}
		try{
			sql.execute ('DROP PROCEDURE IF EXISTS sp_check_vm')
			sql.execute ('CREATE PROCEDURE sp_check_vm() BEGIN UPDATE virtual_machine_execution SET status = \'RECONNECTING\' where CURRENT_TIMESTAMP > DATE_ADD(last_report, INTERVAL 4 MINUTE) AND status = \'DEPLOYED\' AND id > 0;'+ 
															'UPDATE virtual_machine_execution SET status = \'FAILED\' where CURRENT_TIMESTAMP > DATE_ADD(last_report, INTERVAL 10 MINUTE) AND status = \'RECONNECTING\'; END')
			sql.execute ('CREATE EVENT if not exists update_vms ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP DO EXECUTE sp_check_vm')
		}catch(Exception e){println e.message}
			//sql.execute 'update physical_machine set state = \'OFF\', with_user=0;'	
		sql.close();
		
	}

}
