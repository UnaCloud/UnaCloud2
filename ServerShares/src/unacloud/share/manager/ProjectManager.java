package unacloud.share.manager;


import unacloud.share.queue.QueueMessageReceiver;
import unacloud.share.utils.ConfigurationReader;


/**
 * Abstract class to be extended by services control classes 
 * @author Cesar
 *
 */
public abstract class ProjectManager {

	/**
	 * Agent Communication port
	 */
	private int COM_PORT;	
	
	/**
	 * Reads configuration from file
	 */
	protected ConfigurationReader reader;
	
	/**
	 * Queue process message object
	 */
	protected QueueMessageReceiver queueReceiver;
	
	public ProjectManager() {
		try {
			loadVariables();
			startQueueService();
			startDatabaseService();
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
		COM_PORT = reader.getIntegerVariable(getPortNameVariable());
	}
	
	/**
	 * Return the name of properties file to load configuration
	 * @return String 
	 */
	protected abstract String getPropetiesFileName();
	
	/**
	 * Return the name of agent port variable to be load in configuration
	 * @return
	 */
	protected abstract String getPortNameVariable();
	
	/**
	 * Method to return list of variable to be load to configuration
	 * @return
	 */
	protected abstract String[] getVariableList();
	
	/**
	 * Return the agent port configured for communication
	 * @return
	 */
	public int getPort(){
		return COM_PORT;
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
