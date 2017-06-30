package uniandes.unacloud.file;

import uniandes.unacloud.share.queue.QueueMessageReceiver;
import uniandes.unacloud.share.queue.QueueRabbitManager;
import uniandes.unacloud.share.utils.EnvironmentManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.share.db.DatabaseConnection;
import uniandes.unacloud.share.manager.ProjectManager;
import uniandes.unacloud.file.net.AgentServerSocket;
import uniandes.unacloud.file.net.FileServerSocket;
import uniandes.unacloud.file.queue.QueueMessageFileProcessor;

/**
 * Initializes and control all services in project. It class extends from Project Manager class and works as a Singleton class.
 * 
 * @author CesarF
 */
public class FileManager extends ProjectManager {
	
	/**
	 * File manager instance
	 */
	private static FileManager fileManager;
	
	/**
	 * Number of initial and minimum active connections to database
	 */
	private static final int POOL_SIZE = 5;

	/**
	 * Number of concurrent threads to process request for files
	 */
	private static final int CONCURRENT_THREADS_FILE = 30;
	
	/**
	 * Number of concurrent threads to process messages from agents
	 */
	private static final int CONCURRENT_THREADS_AGENT = 30;
	
	/**
	 * Number of concurrent threads to process messages from queue
	 */
	private static final int CONCURRENT_THREADS_QUEUE = 50;
	
	
	public FileManager() {
		super();
	}
	
	/**
	 * Returns file manager instance
	 * @return instance
	 */
	public static FileManager getInstance() {
		try {
			if (fileManager == null)
				fileManager = new FileManager();
			return fileManager;
		} catch (Exception e) {
			return null;
		}		
	}	

	@Override
	protected String getPropetiesFileName() {	
		System.out.println("Load file: " + EnvironmentManager.getConfigPath() + UnaCloudConstants.FILE_CONFIG);
		return EnvironmentManager.getConfigPath() + UnaCloudConstants.FILE_CONFIG;	
	}

	@Override
	protected String[] getVariableList() {
		return new String[]{
				UnaCloudConstants.VERSION_MANAGER_PORT,
				UnaCloudConstants.FILE_SERVER_PORT,
				UnaCloudConstants.QUEUE_USER,
				UnaCloudConstants.QUEUE_PASS,
				UnaCloudConstants.QUEUE_IP,
				UnaCloudConstants.QUEUE_PORT,
				UnaCloudConstants.DB_NAME,
				UnaCloudConstants.DB_PASS,
				UnaCloudConstants.DB_PORT,
				UnaCloudConstants.DB_IP,
				UnaCloudConstants.DB_USERNAME,
				UnaCloudConstants.AGENT_PORT};
	}

	@Override
	protected void startDatabaseService() throws Exception {
		System.out.println("Start database service");
		connection = new DatabaseConnection();
		connection.connect(
				reader.getStringVariable(UnaCloudConstants.DB_NAME), 
				reader.getIntegerVariable(UnaCloudConstants.DB_PORT),
				reader.getStringVariable(UnaCloudConstants.DB_IP), 
				reader.getStringVariable(UnaCloudConstants.DB_USERNAME), 
				reader.getStringVariable(UnaCloudConstants.DB_PASS),
				POOL_SIZE);
		connection.getConnection().close();		
	}

	@Override
	protected void startQueueService() throws Exception {
		System.out.println("Start queue service");
		QueueRabbitManager rabbitManager = new QueueRabbitManager(
				reader.getStringVariable(UnaCloudConstants.QUEUE_USER),
				reader.getStringVariable(UnaCloudConstants.QUEUE_PASS), 
				reader.getStringVariable(UnaCloudConstants.QUEUE_IP), 
				reader.getIntegerVariable(UnaCloudConstants.QUEUE_PORT),
				UnaCloudConstants.QUEUE_FILE);
		queueReceiver = new QueueMessageReceiver();
		queueReceiver.createConnection(rabbitManager);
		queueReceiver.startReceiver(new QueueMessageFileProcessor(CONCURRENT_THREADS_QUEUE));	
	}

	@Override
	protected void startCommunicationService() throws Exception {
		System.out.println("Start communication service");
		new FileServerSocket(reader.getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT), CONCURRENT_THREADS_FILE).start();
		new AgentServerSocket(reader.getIntegerVariable(UnaCloudConstants.VERSION_MANAGER_PORT), CONCURRENT_THREADS_AGENT).start();
	}

}
