package uniandes;


import manager.ProjectManager;

import com.losandes.utils.Constants;

import db.DatabaseConnection;
import queue.QueueMessageReceiver;
import queue.QueueRabbitManager;
import unacloud.utils.UnaCloudVariables;
import uniandes.queue.QueueMessageProcessor;

/**
 * Class used to start and manage services in Control project
 * @author Cesar
 *
 */
public class ControlManager extends ProjectManager{
	
	public static ControlManager control;
	
	public ControlManager() {
		super();
	}
	
	public static ControlManager getInstance(){
		try {
			if(control==null)control = new ControlManager();
			return control;
		} catch (Exception e) {
			return null;
		}		
	}	
	
	@Override
	protected String getPortNameVariable() {
		return Constants.AGENT_PORT;
	}

	@Override
	protected String getPropetiesFileName() {
		return "controlConfig.properties";
	}

	/**
	 * Return the list of variables required by services
	 * @return
	 */
	@Override
	protected String[] getVariableList() {
		return new String[]{Constants.QUEUE_USERNAME,Constants.QUEUE_PASS,Constants.QUEUE_URL,Constants.QUEUE_PORT,Constants.DB_NAME,
				Constants.DB_PASS,Constants.DB_PORT,Constants.DB_URL,Constants.DB_USERNAME,Constants.AGENT_PORT};
    }

	/**
	 * Initial configuration for Database connection pool
	 * @throws Exception
	 */
	@Override
	protected void startDatabaseService() throws Exception {
		DatabaseConnection.getInstance().connect(reader.getStringVariable(Constants.DB_NAME), reader.getIntegerVariable(Constants.DB_PORT),
				reader.getStringVariable(Constants.DB_URL), reader.getStringVariable(Constants.DB_USERNAME), reader.getStringVariable(Constants.DB_PASS));
	}

	/**
	 * Start the queue connection service to receive messages
	 * @throws Exception
	 */
	@Override
	protected void startQueueService() throws Exception {
		QueueRabbitManager rabbitManager = new QueueRabbitManager(reader.getStringVariable(Constants.QUEUE_USERNAME),
				reader.getStringVariable(Constants.QUEUE_PASS), reader.getStringVariable(Constants.QUEUE_URL), 
				reader.getIntegerVariable(Constants.QUEUE_PORT), UnaCloudVariables.QUEUE_CONTROL);
		queueReceiver = new QueueMessageReceiver();
		queueReceiver.createConnection(rabbitManager);
		queueReceiver.startReceiver(new QueueMessageProcessor());		
	}
	

	@Override
	protected void startCommunicationService() throws Exception {
		
	}
	

}
