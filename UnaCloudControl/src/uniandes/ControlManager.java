package uniandes;

import unacloud.share.manager.ProjectManager;

import com.losandes.utils.UnaCloudConstants;

import unacloud.share.db.DatabaseConnection;
import unacloud.share.queue.QueueMessageReceiver;
import unacloud.share.queue.QueueRabbitManager;
import uniandes.communication.PmMessageReceiver;
import uniandes.communication.VmMessageReceiver;
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
		return UnaCloudConstants.AGENT_PORT;
	}

	@Override
	protected String getPropetiesFileName() {
		return UnaCloudConstants.CONTROL_CONFIG;
	}

	/**
	 * Return the list of variables required by services
	 * @return
	 */
	@Override
	protected String[] getVariableList() {
		return new String[]{UnaCloudConstants.QUEUE_USER,UnaCloudConstants.QUEUE_PASS,UnaCloudConstants.QUEUE_IP,UnaCloudConstants.QUEUE_PORT,UnaCloudConstants.DB_NAME,
				UnaCloudConstants.DB_PASS,UnaCloudConstants.DB_PORT,UnaCloudConstants.DB_IP,UnaCloudConstants.DB_USERNAME,UnaCloudConstants.AGENT_PORT};
    }

	/**
	 * Initial configuration for Database connection pool
	 * @throws Exception
	 */
	@Override
	protected void startDatabaseService() throws Exception {
		connection = new DatabaseConnection();
		connection.connect(reader.getStringVariable(UnaCloudConstants.DB_NAME), reader.getIntegerVariable(UnaCloudConstants.DB_PORT),
				reader.getStringVariable(UnaCloudConstants.DB_IP), reader.getStringVariable(UnaCloudConstants.DB_USERNAME), reader.getStringVariable(UnaCloudConstants.DB_PASS));
	}

	/**
	 * Start the queue connection service to receive messages
	 * @throws Exception
	 */
	@Override
	protected void startQueueService() throws Exception {
		QueueRabbitManager rabbitManager = new QueueRabbitManager(reader.getStringVariable(UnaCloudConstants.QUEUE_USER),
				reader.getStringVariable(UnaCloudConstants.QUEUE_PASS), reader.getStringVariable(UnaCloudConstants.QUEUE_IP), 
				reader.getIntegerVariable(UnaCloudConstants.QUEUE_PORT), UnaCloudConstants.QUEUE_CONTROL);
		queueReceiver = new QueueMessageReceiver();
		queueReceiver.createConnection(rabbitManager);
		queueReceiver.startReceiver(new QueueMessageProcessor());		
	}
	

	@Override
	protected void startCommunicationService() throws Exception {
		new PmMessageReceiver(reader.getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_PM_PORT),5).start();
		new VmMessageReceiver(reader.getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT),3).start();
	}	

}
