package uniandes.unacloud.agent.utils;

import java.io.IOException;

import uniandes.unacloud.common.utils.ConfigurationReader;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Singleton class to control properties file
 * @author CesarF
 *
 */
public class VariableManager {
	
	private ConfigurationReader propLocal;
	private ConfigurationReader propGlobal;
	
	private static VariableManager instance;
	
	public static VariableManager getInstance() {
		try {
			if(instance==null)instance = new VariableManager();
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public VariableManager() throws IOException {
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
