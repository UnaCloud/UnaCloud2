package uniandes.unacloud.web.services.init

import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;


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
			sql.execute 'CREATE TRIGGER '
						+ 'create_request_events AFTER INSERT ON execution '
							+ 'FOR EACH ROW BEGIN '
								+ 'INSERT INTO execution_history (state_id, change_time, execution_id, version, message) '
								+ 'VALUES (NEW.state_id, CURRENT_TIMESTAMP, NEW.id, 1, NEW.message); '
							+ 'END;'
		} catch(Exception e) {
			print 'create_request_events is already created'
		}                 
		try {
			sql.execute 'CREATE TRIGGER '
						+ 'save_request_events AFTER UPDATE ON execution '
							+ 'FOR EACH ROW BEGIN '
								+ 'IF NEW.state_id <> OLD.state_id THEN '
									+ 'SELECT @current := CURRENT_TIMESTAMP; '
									+ 'INSERT INTO execution_history (state_id, change_time, execution_id, version, message) VALUES (NEW.state_id, @current, NEW.id, 1, NEW.message); '
																	
									+ 'SELECT @failed := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.FAILED.name() + '\'; '
									+ 'SELECT @finished := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.FINISHED.name() + '\'; '
									+ 'SELECT @request_copy := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.REQUEST_COPY.name() + '\'; '
									+ 'SELECT @copying := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.COPYING.name() + '\'; '
									+ 'SELECT @deployed := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.DEPLOYED.name() + '\'; '
									+ 'SELECT @finishing := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.FINISHING.name() + '\'; ' 
									+ 'SELECT @reconnecting := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.RECONNECTING.name() + '\'; '
									+ 'SELECT @deploying := id FROM execution_state WHERE state = \'' + ExecutionStateEnum.DEPLOYING.name() + '\'; '
									//If execution has finished all ips must be released
									+ 'IF NEW.state_id == @failed OR (NEW.state_id == @finished AND OLD.state_id <> @failed) THEN '
										+ "UPDATE ip SET state = \'" + IPEnum.AVAILABLE.name() + "\' "
											+ "WHERE id IN (SELECT ip_id FROM net_interface WHERE execution_id = NEW.id) "
												+ "AND id > 0; "
									//If copy image to server failed copy should be deleted 
									+ 'ELSEIF OLD.state_id == @request_copy AND NEW.state_id = @deployed THEN'
										+ 'DELETE FROM image WHERE id = OLD.copy_to; '
									//If copy image to server failed copy should be disable to avoid other fails
									+ 'ELSEIF OLD.state_id == @copying AND NEW.state_id = @failed THEN'
										+ 'UPDATE image SET state = \'' + ImageEnum.UNAVAILABLE + '\' WHERE id = OLD.copy_to; '
									+ 'END IF; '
									//If execution start process to finish all stop time must be set
									+ 'IF (OLD.state_id == @reconnecting AND NEW.state_id = @failed) OR (OLD.state_id == @request_copy AND NEW.state_id = @copying) OR (OLD.state_id == @deployed AND NEW.state_id = @finishing) THEN '
										+ 'UPDATE execution SET stop_time = @current, last_report = @current WHERE id = NEW.id; '
									//If execution has been started
									+ 'ELSEIF OLD.state_id == @deploying AND NEW.state_id = @deployed THEN'
										+ 'UPDATE execution SET start_time = @current, stop_time = DATE_ADD(@current, INTERVAL NEW.duration/1000 SECONDS), last_report = @current WHERE id = NEW.id; '
									+ 'ELSE'
										+ 'UPDATE execution SET last_report = @current WHERE id = NEW.id; '
									+ 'END IF;'
								+ 'END IF; '
							+ 'END'
		} catch(Exception e) {
			print 'save_request_events is already created'
		}
		try {
			sql.execute ('DROP PROCEDURE IF EXISTS sp_check_pm')
			sql.execute ('CREATE PROCEDURE sp_check_pm() BEGIN '
							+ 'UPDATE physical_machine SET state = \'' + PhysicalMachineStateEnum.OFF.name() + '\' , with_user = false WHERE CURRENT_TIMESTAMP > DATE_ADD(last_report, INTERVAL 4 MINUTE) AND state = \'' + PhysicalMachineStateEnum.ON.name() + '\' AND id > 0; '
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
