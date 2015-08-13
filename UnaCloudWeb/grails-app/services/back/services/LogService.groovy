package back.services

import unacloud2.Log

class LogService {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Creates a new log message in database
	 * @param origin origin of the log message
	 * @param component specific component which is related to message
	 * @param message 
	 */
	
    def createLog(String origin, String component, String message) {
		new Log(origin: origin, component:component, message:message, timestamp: new Date()).save()
    }
}
