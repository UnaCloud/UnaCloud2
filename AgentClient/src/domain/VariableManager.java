package domain;

import java.io.IOException;

import com.losandes.utils.ConfigurationReader;
import com.losandes.utils.UnaCloudConstants;

/**
 * Singleton class to control properties file
 * @author Cesar
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
	
	public ConfigurationReader getLocal(){
		return propLocal;
	}
	
	public ConfigurationReader getGlobal(){
		return propGlobal;
	}

}
