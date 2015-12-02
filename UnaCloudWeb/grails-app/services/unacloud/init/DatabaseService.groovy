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
			sql.execute 'CREATE TRIGGER create_request_events AFTER INSERT ON virtual_machine_execution FOR EACH ROW BEGIN INSERT INTO execution_request (status, request_time, execution_id) VALUES (NEW.status, CURRENT_TIMESTAMP, NEW.id); END;'
		}catch(Exception e){print 'create_request_events is already created'}
		try{
			sql.execute 'CREATE TRIGGER save_request_events AFTER UPDATE ON virtual_machine_execution FOR EACH ROW BEGIN IF NEW.status <> OLD.status THEN INSERT INTO execution_request (status, request_time, execution_id) VALUES (NEW.status, CURRENT_TIMESTAMP, NEW.id); END IF; END'
		}catch(Exception e){print 'save_request_events is already created'}
		sql.close();
	}

}
