package uniandes.unacloud;


import queue.QueueMessageReceiver;
import queue.QueueRabbitManager;

import com.losandes.utils.Constants;

import db.DatabaseConnection;
import manager.ProjectManager;

/**
 * Initializes and control all services in project
 * @author Cesar
 *
 */
public class FileManager extends ProjectManager{
	
	private static FileManager fileManager;
	
	public FileManager() {
		super();
	}
	
	public static FileManager getInstance(){
		try {
			if(fileManager==null)fileManager = new FileManager();
			return fileManager;
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
		return "fileManager.properties";
	}

	@Override
	protected String[] getVariableList() {
		return new String[]{Constants.QUEUE_USERNAME,Constants.QUEUE_PASS,Constants.QUEUE_URL,Constants.QUEUE_PORT,Constants.DB_NAME,
				Constants.DB_PASS,Constants.DB_PORT,Constants.DB_URL,Constants.DB_USERNAME,Constants.AGENT_PORT};
	}

	@Override
	protected void startDatabaseService() throws Exception {
		DatabaseConnection.getInstance();
		DatabaseConnection.getInstance().connect(reader.getStringVariable(Constants.DB_NAME), reader.getIntegerVariable(Constants.DB_PORT),
				reader.getStringVariable(Constants.DB_URL), reader.getStringVariable(Constants.DB_USERNAME), reader.getStringVariable(Constants.DB_PASS));
	}

	@Override
	protected void startQueueService() throws Exception {
		QueueRabbitManager rabbitManager = new QueueRabbitManager(reader.getStringVariable(Constants.QUEUE_USERNAME),
				reader.getStringVariable(Constants.QUEUE_PASS), reader.getStringVariable(Constants.QUEUE_URL), 
				reader.getIntegerVariable(Constants.QUEUE_PORT), "AGENT_CONTROL");
		queueReceiver = new QueueMessageReceiver();
		queueReceiver.createConnection(rabbitManager);
		queueReceiver.startReceiver(new QueueMessageFileProcessor());	
	}

	@Override
	protected void startCommunicationService() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
