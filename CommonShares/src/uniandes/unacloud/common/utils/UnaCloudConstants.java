package uniandes.unacloud.common.utils;

/**
 * Responsible for providing the UnaCloud constant values
 * @author Edgar Eduardo Rosales Rosero
 * @author CesarF
 *
 */
public class UnaCloudConstants {	

	//UnaCloud Configuration
	public static final String MAIN_REPOSITORY = "MAIN_REPOSITORY";	
	public static final String TEMPLATE_PATH = "imageTemplates";
	public static final String LOGS_PATH = "logs";
	public static final String ADMIN_GROUP = "unacloudAdmins";
	public static final String USERS_GROUP = "unacloudUsers";
	
	//Server Variables
	public static final String QUEUE_IP = "QUEUE_IP";
	public static final String QUEUE_PORT = "QUEUE_PORT";
	public static final String QUEUE_USER = "QUEUE_USER";
	public static final String QUEUE_PASS = "QUEUE_PASS";
	public static final String QUEUE_CONTROL = "AGENT_CONTROL";
	public static final String QUEUE_FILE = "FILE_MANAGER";
	public static final String DB_USERNAME = "DB_USERNAME";
	public static final String DB_PASS = "DB_PASS";
	public static final String DB_IP = "DB_IP";
	public static final String DB_PORT = "DB_PORT";
	public static final String DB_NAME = "DB_NAME";	
	public static final String FILE_CONFIG = "config.properties";
	public static final String ROOT_PATH = "ROOT_PATH";
	
	//UnaCloudControl Variables
	public static final String CONTROL_SERVER_IP = "CONTROL_SERVER_IP"; //Agent
	public static final String CONTROL_MANAGE_PM_PORT = "CONTROL_MANAGE_PM_PORT"; //Agent
	public static final String CONTROL_MANAGE_VM_PORT = "CONTROL_MANAGE_VM_PORT"; //Agent
	public static final String AGENT_PORT = "AGENT_PORT"; //Agent
	
	//UnaClousFileManager Variables
	public static final String WEB_FILE_SERVER_URL = "WEB_FILE_SERVER_URL";
	public static final String FILE_SERVER_PORT = "FILE_SERVER_PORT"; //Agent
	public static final String FILE_SERVER_IP = "FILE_SERVER_IP"; //Agent
	public static final String VERSION_MANAGER_PORT = "VERSION_MANAGER_PORT"; //Agent
	public static final String TORRENT_CLIENT_PORTS = "TORRENT_CLIENT_PORTS"; //Agent
	public static final String FILE_SERVER_TORRENT_PORT = "FILE_SERVER_TORRENT_PORT"; //Agent
		
	//Communication agents and File Manager
	public static final int REQUEST_IMAGE = 1;
	public static final int SEND_IMAGE = 2;
	public static final int REQUEST_AGENT_VERSION = 3;
	public static final int THANKS = 4;
	public static final int GIVE_ME_FILES = 5;
	
	//UnaCloudWeb Variables
	public static final String WEB_SERVER_URL = "WEB_SERVER_URL";
	public static final String AGENT_VERSION = "AGENT_VERSION";
	public static final String VM_DEFAULT_ALLOCATOR = "VM_DEFAULT_ALLOCATOR";
	public static final String DEFAULT_USER_PASSWORD = "DEFAULT_USER_PASSWORD";
		
	//Agent
	public static final String VMRUN_PATH = "VMRUN_PATH";
	public static final String VBOX_PATH = "VBOX_PATH";
	public static final String DOCKER_PATH = "DOCKER_PATH";
	public static final String VM_REPO_PATH = "VM_REPO_PATH";
	public static final String REGISTERED = "REGISTERED";
	public static final String DATA_PATH = "DATA_PATH";
	public static final int TEST = 2;
	public static final int RUN = 1;
	public static final int DELAY = 6;
	public static final String UPDATER_JAR = "ClientUpdater.jar";
	public static final String AGENT_JAR = "UnaClient.jar";
	public static final String GLOBAL_FILE = "global.properties";
	public static final String LOCAL_FILE = "local.properties";
	public static final String VERSION_FILE = "version.txt";
	public static final String AGENT_OUT_LOG = "unacloud_out.log";
	public static final String AGENT_ERROR_LOG = "unacloud_err.log";
	public static final String CONTROL_OUT_LOG = "unacloud_control_out.log";
	public static final String CONTROL_ERROR_LOG = "unacloud_control_err.log";
	
    //UnaCloud communication and operation constants
	public static final String TRANSMISSION_PROTOCOL = "TRANSMISSION_PROTOCOL";
    public static final String OK_MESSAGE = "Ok: ";
    public static final String SUCCESSFUL_OPERATION = "Successful operation";
    public static final String ERROR_MESSAGE = "Error: ";
    public static final String UNSUCCESSFUL_OPERATION = "Unsuccessful operation";
    public static final String TEMP_FILE = "temp";
    public static final String FILE_EXTENSION = ".txt";
    public static final String DEFAULT_IMG_NAME = "unacloudbase";
    public static final String DOUBLE_QUOTE = "\"";
	public static final int SOCKET_TIME_OUT = 4000;
 
}
