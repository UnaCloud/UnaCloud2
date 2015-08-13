package monitoring;

import connection.MonitorDatabaseConnection;
import back.services.VariableManagerService;

public class MonitoringDBServerConnection extends MonitorDatabaseConnection{

	@Override
	public void callVariables() {
		VariableManagerService variableManagerService = new VariableManagerService();
		ip = variableManagerService.getStringValue("MONITORING_SERVER_IP");
	    port = variableManagerService.getIntValue("MONITORING_SERVER_PORT");
	    name = variableManagerService.getStringValue("MONITORING_DATABASE_NAME");
	    user = variableManagerService.getStringValue("MONITORING_DATABASE_USER");
	    password = variableManagerService.getStringValue("MONITORING_DATABASE_PASSWORD");
	}

	public boolean testConnection(){
		if(ip==null||name==null)return false;
		try {
			this.generateConnection();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
