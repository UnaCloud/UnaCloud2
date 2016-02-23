package unacloud

import com.losandes.utils.UnaCloudConstants;

import grails.transaction.Transactional

@Transactional
class ServerVariableService {

	/**
	 * Return the default allocator configured in database
	 * @return
	 */
    def getDefaultAllocator() {
		return ServerVariable.findByName(UnaCloudConstants.VM_DEFAULT_ALLOCATOR)
    }
	
	/**
	 * Return the current web server file url
	 * @return
	 */
	def getUrlFileManager(){
		return ServerVariable.findByName(UnaCloudConstants.WEB_FILE_SERVER_URL).variable
	}
}
