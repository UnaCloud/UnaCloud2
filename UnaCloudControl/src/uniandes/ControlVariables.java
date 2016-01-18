package uniandes;

/**
 * Pages to list all variables necessary to control project
 * @author Cesar
 *
 */
public class ControlVariables {
	
	public static final String QUEUE_USERNAME = "QUSERNAME";
	public static final String QUEUE_PASS = "QPASS";
	public static final String QUEUE_URL = "QURL";
	public static final String QUEUE_PORT = "QPORT";
	public static final String DB_USERNAME = "DBUSERNAME";
	public static final String DB_PASS = "DBPASS";
	public static final String DB_URL = "DBURL";
	public static final String DB_PORT = "DBPORT";
	public static final String DB_NAME = "DBNAME";
	
	public static String[] list(){
		return new String[]{QUEUE_USERNAME,QUEUE_PASS,QUEUE_URL,QUEUE_PORT,DB_NAME,DB_PASS,DB_PORT,DB_PORT,DB_USERNAME};
	}

}
