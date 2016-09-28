package uniandes.unacloud.common.utils;

import java.io.File;

/**
 * Responsible for providing the UnaCloud constant values
 * @author Edgar Eduardo Rosales Rosero
 * @author CesarF
 * 
 */
public class Constants {
	
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
       
    
    //UnaCloud Client tools constants
    public static final String WOL_CMD = "WolCmd";
    public static final String MULTICAST_GET_LOSED="MULTICAST_GET_LOSED";    
    

}//end of Constants
