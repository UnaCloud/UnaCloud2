package com.losandes.utils;

import java.io.File;

/**
 * @author Edgar Eduardo Rosales Rosero
 * @author Cesar
 * Responsible for providing the UnaCloud Server constant values
 */
public class Constants {
	//UnaCloud Configuration
	public static final String MAIN_REPOSITORY = "Main Repository";
	public static final String TEMPLATE_PATH = "imageTemplates";
	public static final String ADMIN_GROUP = "unacloudAdmins";
	public static final String USERS_GROUP = "unacloudUsers";
	
    //UnaCloud communication and operation constants
    public static final String OK_MESSAGE = "Ok: ";
    public static final String SUCCESSFUL_OPERATION = "Successful operation";
    public static final String ERROR_MESSAGE = "Error: ";
    public static final String UNSUCCESSFUL_OPERATION = "Unsuccessful operation";
    public static final String FATAL_ERROR_MESSAGE = "Fatal Error: ";
    public static final String NOTHING_AVAILABLE = "N/A";
    public static final String CLIENT_ALIVE_MESSAGE = "ALIVE";
    public static final int DGRAM_LENGTH = 1024*10;
    public static final int CLOUDER_CLIENT_TIMEOUT= 1500; //milliseconds
    public static final String MESSAGE_SEPARATOR_TOKEN = "%%";
                
    //UnaCloud Client location constants
    public static final String BIN="bin";
    public static final String SERVER_PARAMETERS="server_parameters";
    public static final String LOCAL_PARAMETERS="local_parameters";
    public static final String KEY="key";
    public static final String PATH_SPACE=" ";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String SEPARATOR = File.pathSeparator;
    public static final String PATH_SEPARATOR = File.separator;
    public static final String JAR_NAME="/UnaCloudClient.jar";
    
    //UnaCloud Client VMware Workstation constants
    public static final String VM_WARE_WORKSTATION="Workstation";
    public static final String VM_WARE_PLAYER="Player";
    public static final String VIRTUAL_BOX="VBox";
    public static final String VMW_RUN_FILE="\\vmrun.exe";
    public static final String VMW_VMX_EXTENSION=".vmx";
    public static final String VMW_TURN_ON="start";
    public static final String VMW_TURN_OFF="stop";
    public static final String VMW_RESTART="reset";
    public static final String VMW_LIST="list";
    public static final String VMW_VMX_ENCONDING = "windows-1252";
    public static final String VMW_VMX_CPU = "numvcpus";
    public static final String VMW_VMX_MEMORY = "memsize";
    public static final String VMW_VMX_HW = "virtualHW.version";
    public static final String VMW_VMX_HW_VER = "7";
    
    //UnaCloud Client operating system operations constants
    public static final String WINDOWS_TURN_OFF_COMMAND = "c:\\windows\\system32\\shutdown.exe -s -t 60";
    public static final String WINDOWS_RESTART_COMMAND = "c:\\windows\\system32\\shutdown.exe -r -t 30";
    public static final String WINDOWS_LOGOUT_COMMAND = "c:\\windows\\system32\\shutdown.exe -l -f";
    public static final String LINUX_TURN_OFF_FILE_COMMAND = "turnOffLinux.sh ";
    public static final String LINUX_RESTART_FILE_COMMAND = "restartLinux.sh ";
    public static final String LINUX_LOGOUT_FILE_COMMAND = "logoutLinux.sh ";
    public static final String MAC_PERL_COMMAND = "/usr/bin/perl";
    public static final String MAC_TURN_OFF_FILE_COMMAND = "turnOffMac.sh";
    public static final String MAC_RESTART_FILE_COMMAND = "restartMac.sh";
    public static final String MAC_LOGOUT_FILE_COMMAND = "logoutMac.pl";
    
    //UnaCloud Client tools constants
    public static final String WOL_CMD = "WolCmd";
    public static final String MULTICAST_GET_LOSED="MULTICAST_GET_LOSED";
    
    //UnaCloudControl
    public static final String QUEUE_USERNAME = "QUSERNAME";
	public static final String QUEUE_PASS = "QPASS";
	public static final String QUEUE_URL = "QURL";
	public static final String QUEUE_PORT = "QPORT";	
	public static final String DB_USERNAME = "DBUSERNAME";
	public static final String DB_PASS = "DBPASS";
	public static final String DB_URL = "DBURL";
	public static final String DB_PORT = "DBPORT";
	public static final String DB_NAME = "DBNAME";	
	public static final String AGENT_PORT = "APORT";
	public static final int AGENT_QUANTITY_MESSAGE = 5;
	
	public static final int REQUEST_IMAGE = 1;
	public static final int SEND_IMAGE = 2;

}//end of Constants
