package unacloud.share.manager;

import java.sql.Connection;
import java.sql.SQLException;

import unacloud.share.db.DatabaseConnection;
import unacloud.share.queue.QueueMessageReceiver;
import unacloud.share.utils.ConfigurationReader;


/**
 * Abstract class to be extended by services control classes 
 * @author CesarF
 *
 */
public abstract class ProjectManager {

	/**
	 * Reads configuration from file
	 */
	protected ConfigurationReader reader;
	
	/**
	 * Queue process message object
	 */
	protected QueueMessageReceiver queueReceiver;
	
	/**
	 * Database connection processes
	 */
	protected DatabaseConnection connection;
	
	public ProjectManager() {
		try {
			loadVariables();
			startQueueService();
			startDatabaseService();
			startCommunicationService();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Method to load variable necessary for configuration
	 * @throws Exception 
	 */
	private void loadVariables() throws Exception{
		reader = new ConfigurationReader(getPropetiesFileName(), getVariableList());
	}
	
	/**
	 * Returns the name of properties file to load configuration
	 * @return String 
	 */
	protected abstract String getPropetiesFileName();
	
	
	/**
	 * Method to return list of variable to be load to configuration
	 * @return
	 */
	protected abstract String[] getVariableList();
	
	/**
	 * Returns current database connection
	 * @return
	 */
	public Connection getDBConnection(){
		try {
			return connection.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Method used to start queue communication service
	 */
	protected abstract void startQueueService() throws Exception;
	
	/**
	 * Method used to start database communication service
	 */
	protected abstract void startDatabaseService() throws Exception;
	
	/**
	 * Method used to start agent communication service
	 */
	protected abstract void startCommunicationService() throws Exception;
}
