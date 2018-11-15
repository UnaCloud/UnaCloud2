package uniandes.unacloud.file;

import java.sql.Connection;

import uniandes.unacloud.share.queue.QueueMessageReceiver;
import uniandes.unacloud.share.queue.QueueRabbitManager;
import uniandes.unacloud.share.utils.EnvironmentManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.share.db.DatabaseConnection;
import uniandes.unacloud.share.db.ServerVariableManager;
import uniandes.unacloud.share.db.StorageManager;
import uniandes.unacloud.share.manager.ProjectManager;
import uniandes.unacloud.file.net.AgentServerSocket;
import uniandes.unacloud.file.net.FileServerSocket;
import uniandes.unacloud.file.net.torrent.TorrentTracker;
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
	private static final int CONCURRENT_THREADS_FILE = 50;
	
	/**
	 * Number of concurrent threads to process messages from agents
	 */
	private static final int CONCURRENT_THREADS_AGENT = 50;
	
	/**
	 * Number of concurrent threads to process messages from queue
	 */
	private static final int CONCURRENT_THREADS_QUEUE = 50;
	
	/**
	 * Creates a File Manager
	 */
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
				UnaCloudConstants.DB_NAME,
				UnaCloudConstants.DB_PASS,
				UnaCloudConstants.DB_PORT,
				UnaCloudConstants.DB_IP,
				UnaCloudConstants.DB_USERNAME};
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
		try (Connection con = connection.getConnection()){
			String queueUser = ServerVariableManager.getVariable(con, UnaCloudConstants.QUEUE_USER).getValue();
			String queuePass = ServerVariableManager.getVariable(con, UnaCloudConstants.QUEUE_PASS).getValue();
			String queueIP = ServerVariableManager.getVariable(con, UnaCloudConstants.QUEUE_IP).getValue();
			int queuePort = Integer.parseInt(ServerVariableManager.getVariable(con, UnaCloudConstants.QUEUE_PORT).getValue());
			QueueRabbitManager rabbitManager = new QueueRabbitManager(queueUser, queuePass, queueIP, queuePort,
					UnaCloudConstants.QUEUE_FILE);
			queueReceiver = new QueueMessageReceiver();
			queueReceiver.createConnection(rabbitManager);
			queueReceiver.startReceiver(new QueueMessageFileProcessor(CONCURRENT_THREADS_QUEUE));	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	protected void startCommunicationService() throws Exception {
		
		System.out.println("Start communication service");
		
		try (Connection con = connection.getConnection()){
			int fileServerPort = Integer.parseInt(ServerVariableManager.getVariable(con, UnaCloudConstants.FILE_SERVER_PORT).getValue());
			int versionPort = Integer.parseInt(ServerVariableManager.getVariable(con, UnaCloudConstants.VERSION_MANAGER_PORT).getValue());
			
			new FileServerSocket(fileServerPort, CONCURRENT_THREADS_FILE).start();			
			new AgentServerSocket(versionPort, CONCURRENT_THREADS_AGENT).start();
			
			int[] ports = null;
			String portString = ServerVariableManager.getVariable(con, UnaCloudConstants.TORRENT_CLIENT_PORTS).getValue();
			if (portString != null) {
				String [] data = portString.split(",");
				ports = new int[data.length];
				for (int i = 0; i < data.length; i++)
					ports[i] = Integer.parseInt(data[i]);
			}
			
			int torrentPort = Integer.parseInt(ServerVariableManager.getVariable(con, UnaCloudConstants.FILE_SERVER_TORRENT_PORT).getValue());
			String fileServerIP = ServerVariableManager.getVariable(con, UnaCloudConstants.FILE_SERVER_IP).getValue();

			String mainRepo = StorageManager.getRepositoryByName(UnaCloudConstants.MAIN_REPOSITORY, con).getRoot();
			TorrentTracker.getInstance().startService(torrentPort, fileServerIP, mainRepo,	ports);

		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	
	}

}
