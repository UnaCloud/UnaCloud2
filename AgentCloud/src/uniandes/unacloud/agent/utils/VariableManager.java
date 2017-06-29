package uniandes.unacloud.agent.utils;

import java.io.IOException;

import uniandes.unacloud.common.utils.ConfigurationReader;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Singleton class to control properties files
 * @author CesarF
 *
 */
public class VariableManager {
	
	/**
	 * Reader for local configuration
	 */
	private ConfigurationReader propLocal;
	/**
	 * Reader for global configuration
	 */
	private ConfigurationReader propGlobal;
	
	/**
	 * Singleton instance
	 */
	private static VariableManager instance;
	
	/**
	 * Gets instance
	 * @return instance
	 */
	public static VariableManager getInstance() {
		try {
			if (instance == null) 
			   instance = new VariableManager();
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 *  Class constructor
	 * @throws IOException
	 */
	private VariableManager() throws IOException {
		propLocal = new ConfigurationReader(UnaCloudConstants.LOCAL_FILE);
		propGlobal = new ConfigurationReader(UnaCloudConstants.GLOBAL_FILE);
	}
	
	/**
	 * Returns local configuration reader
	 * @return configuration reader
	 */
	public ConfigurationReader getLocal(){
		return propLocal;
	}
	
	/**
	 * Returns global configuration reader
	 * @return configuration reader
	 */
	public ConfigurationReader getGlobal(){
		return propGlobal;
	}

}
