package uniandes;

import unacloud.share.manager.ProjectManager;

import com.losandes.utils.UnaCloudConstants;

import unacloud.share.db.DatabaseConnection;
import unacloud.share.queue.QueueMessageReceiver;
import unacloud.share.queue.QueueRabbitManager;
import unacloud.share.utils.EnvironmentManager;
import uniandes.communication.PmMessageReceiver;
import uniandes.communication.VmMessageReceiver;
import uniandes.queue.QueueMessageProcessor;

/**
 *  Initializes and control all services in project. It class extends from Project Manager class and works as a Singleton class.
 * @author CesarF
 *
 */
public class ControlManager extends ProjectManager{
	
	/**
	 * instance of control manager
	 */
	private static ControlManager control;
	private QueueMessageProcessor processor;
	
	public ControlManager() {
		super();
	}
	
	/**
	 * Returns control  manager instance
	 * @return instance
	 */
	public static ControlManager getInstance(){
		try {
			if(control==null)control = new ControlManager();
			return control;
		} catch (Exception e) {
			return null;
		}		
	}	
	
	/**
	 * Returns the agent port configured for communication
	 * @return agent port
	 * @throws Exception 
	 */
	public int getPort() throws Exception{
		return reader.getIntegerVariable(UnaCloudConstants.AGENT_PORT);
	}
	

	@Override
	protected String getPropetiesFileName() {
		System.out.println("Load file: "+EnvironmentManager.getConfigPath()+'/'+UnaCloudConstants.FILE_CONFIG);
		return EnvironmentManager.getConfigPath()+'/'+UnaCloudConstants.FILE_CONFIG;
	}

	/**
	 * Returns the list of variables required by services
	 * @return string array
	 */
	@Override
	protected String[] getVariableList() {
		return new String[]{UnaCloudConstants.CONTROL_MANAGE_VM_PORT,UnaCloudConstants.CONTROL_MANAGE_PM_PORT,UnaCloudConstants.QUEUE_USER,UnaCloudConstants.QUEUE_PASS,UnaCloudConstants.QUEUE_IP,UnaCloudConstants.QUEUE_PORT,UnaCloudConstants.DB_NAME,
				UnaCloudConstants.DB_PASS,UnaCloudConstants.DB_PORT,UnaCloudConstants.DB_IP,UnaCloudConstants.DB_USERNAME,UnaCloudConstants.AGENT_PORT};
    }

	/**
	 * Initial configuration for Database connection pool
	 * @throws Exception
	 */
	@Override
	protected void startDatabaseService() throws Exception {
		System.out.println("Start database service");
		connection = new DatabaseConnection();
		connection.connect(reader.getStringVariable(UnaCloudConstants.DB_NAME), reader.getIntegerVariable(UnaCloudConstants.DB_PORT),
				reader.getStringVariable(UnaCloudConstants.DB_IP), reader.getStringVariable(UnaCloudConstants.DB_USERNAME), reader.getStringVariable(UnaCloudConstants.DB_PASS));
		connection.getConnection().close();		
	}

	/**
	 * Starts the queue connection service to receive messages
	 * @throws Exception
	 */
	@Override
	protected void startQueueService() throws Exception {
		System.out.println("Start queue service");
		QueueRabbitManager rabbitManager = new QueueRabbitManager(reader.getStringVariable(UnaCloudConstants.QUEUE_USER),
				reader.getStringVariable(UnaCloudConstants.QUEUE_PASS), reader.getStringVariable(UnaCloudConstants.QUEUE_IP), 
				reader.getIntegerVariable(UnaCloudConstants.QUEUE_PORT), UnaCloudConstants.QUEUE_CONTROL);
		queueReceiver = new QueueMessageReceiver();
		queueReceiver.createConnection(rabbitManager);
		processor = new QueueMessageProcessor(50);
		queueReceiver.startReceiver(processor);		
	}
	

	/**
	 * Starts the communication socket readers 
	 */
	@Override
	protected void startCommunicationService() throws Exception {
		System.out.println("Start communication service");
		new PmMessageReceiver(reader.getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_PM_PORT),50).start();
		new VmMessageReceiver(reader.getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT),50).start();
	}	
	
	/**
	 * Sends message to agents not have to put message in queue
	 * @param ids
	 */
	public void sendStopMessageExecutions(Long[] ids){
		processor.remoteStopDeploy(ids);
	}

}
