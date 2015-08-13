package back.services

import unacloud2.ServerVariable;


class VariableManagerService {
	
	//TODO crear un cache para rendimiento
    
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * get the variable value as a String
	 * @param key server variable name
	 * @return desired variable as a string
	 */
	def String getStringValue(String key){
		ServerVariable sv=ServerVariable.findByName(key);
		if(sv==null)return null;
		return sv.getVariable();
	}
	
	/**
	 * get the variable value as an integer
	 * @param key server variable name
	 * @return desired variable as an integer
	 */
	def int getIntValue(String key){
		ServerVariable sv=ServerVariable.findByName(key);
		if(sv==null||sv.getVariable()==null||!sv.getVariable().matches("[0-9]+"))return 0;
		return Integer.parseInt(sv.getVariable());
	}
	
	/**
	 * Replaces values of the requested server variables
	 * @param newValues map that contains the server variables to be changed
	 * and its values
	 */
	
	def changeServerVariables(Map newValues){
		for(serverVariable in ServerVariable.all){
			if(!(serverVariable.variable.equals(newValues.getAt(serverVariable.name))))
			serverVariable.variable= newValues.getAt(serverVariable.name)
		}
	}
	
	/**
	 * Increases the minor number of the version server variable
	 */
	def updateAgentVersion(){
		ServerVariable agentVersion= ServerVariable.findByName("AGENT_VERSION")
		int newVerNumber= ((agentVersion.getVariable()-"2.0.") as Integer)+1
		String newVersion=  "2.0."+ newVerNumber
		agentVersion.putAt("variable", newVersion)
	}
}
