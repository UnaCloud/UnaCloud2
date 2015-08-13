package monitoring;

import com.losandes.utils.VariableManager;

import connection.MonitorDatabaseConnection;

public class MonitorDBAgentConnection extends MonitorDatabaseConnection{

	@Override
	public void callVariables() {
		  ip = VariableManager.global.getStringValue("MONITORING_SERVER_IP");
	      port = VariableManager.global.getIntValue("MONITORING_SERVER_PORT");
	      name = VariableManager.global.getStringValue("MONITORING_DATABASE_NAME");
	      user = VariableManager.global.getStringValue("MONITORING_DATABASE_USER");
	      password = VariableManager.global.getStringValue("MONITORING_DATABASE_PASSWORD");	
	}

}
