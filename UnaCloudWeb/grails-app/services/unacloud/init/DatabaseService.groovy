package unacloud.init

import grails.transaction.Transactional
import groovy.sql.Sql

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
	 * 
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
			//TODO Update this event and add virtual images
			//sql.execute 'CREATE EVENT if not exists update_pms ON SCHEDULE EVERY 2 MINUTE STARTS CURRENT_TIMESTAMP DO update physical_machine set state = \'OFF\' where CURRENT_TIMESTAMP > DATE_ADD(last_report, INTERVAL 4 MINUTE);'
		}catch(Exception e){println e.message}
			//sql.execute 'update physical_machine set state = \'OFF\', with_user=0;'	
		sql.close();
	}

}
