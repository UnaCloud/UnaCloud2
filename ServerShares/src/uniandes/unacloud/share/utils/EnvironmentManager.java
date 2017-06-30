package uniandes.unacloud.share.utils;


/**
 * Class used to manage variables in environment to auto-configure server projects
 * @author CesarF
 *
 */
public class EnvironmentManager {
	
	public static final String PATH_CONFIG = "PATH_CONFIG";
	
	/**
	 * Return the path where is located configuration file for project. In case variable is not define 
	 * use root path (/)
	 * @return path where is located configuration file
	 */
	public static String getConfigPath() {
		
		String path = System.getenv().get(PATH_CONFIG);
		if (path == null) return "";
		return path;
	}

}
