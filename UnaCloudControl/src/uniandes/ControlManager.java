package uniandes;


import com.losandes.utils.Constants;

import db.DatabaseConnection;
import queue.QueueRabbitManager;
import uniandes.queue.QueueMessageReceiver;
import utils.ConfigurationReader;

/**
 * Class used to start and manage services in Control project
 * @author Cesar
 *
 */
public class ControlManager {
	
	private int COM_PORT;	
	public static ControlManager control;
	public ConfigurationReader reader;
	
	public ControlManager() throws Exception {
		reader = new ConfigurationReader("controlConfig.properties", variableList());
		COM_PORT = reader.getIntegerVariable(Constants.AGENT_PORT);
	}
	
	public static ControlManager getInstance(){
		try {
			if(control==null)control = new ControlManager();
			return control;
		} catch (Exception e) {
			return null;
		}		
	}
	
	public int getPort(){
		return COM_PORT;
	}
	
	/**
	 * Start the queue connection service to receive messages
	 * @throws Exception
	 */
	public void startQueueService() throws Exception{
		QueueRabbitManager rabbitManager = new QueueRabbitManager(reader.getStringVariable(Constants.QUEUE_USERNAME),
					reader.getStringVariable(Constants.QUEUE_PASS), reader.getStringVariable(Constants.QUEUE_URL), 
					reader.getIntegerVariable(Constants.QUEUE_PORT), "AGENT_CONTROL");
		QueueMessageReceiver.getInstance().createConnection(rabbitManager);
		QueueMessageReceiver.getInstance().startReceiver();				
	}
	
	public void startDatabaseService() throws Exception{
		DatabaseConnection.getInstance().connect(reader.getStringVariable(Constants.DB_NAME), reader.getIntegerVariable(Constants.DB_PORT),
				reader.getStringVariable(Constants.DB_URL), reader.getStringVariable(Constants.DB_USERNAME), reader.getStringVariable(Constants.DB_PASS));
	}
	
	/**
	 * Return the list of variables required by services
	 * @return
	 */
	private String[] variableList(){
		return new String[]{Constants.QUEUE_USERNAME,Constants.QUEUE_PASS,Constants.QUEUE_URL,Constants.QUEUE_PORT,Constants.DB_NAME,
				Constants.DB_PASS,Constants.DB_PORT,Constants.DB_PORT,Constants.DB_USERNAME,Constants.AGENT_PORT};
	}
	

}
