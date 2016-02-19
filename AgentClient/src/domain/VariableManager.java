package domain;

import java.io.IOException;

import com.losandes.utils.ClientConstants;
import com.losandes.utils.ConfigurationReader;

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
		propLocal = new ConfigurationReader(ClientConstants.LOCAL_FILE);
		propGlobal = new ConfigurationReader(ClientConstants.GLOBAL_FILE);
	}
	
	public ConfigurationReader getLocal(){
		return propLocal;
	}
	
	public ConfigurationReader getGlobal(){
		return propGlobal;
	}

}
