package uniandes.unacloud.web.services

import uniandes.unacloud.web.domain.ServerVariable;

import uniandes.unacloud.common.utils.UnaCloudConstants;

import grails.transaction.Transactional

/**
 * This service contains all methods to manage Server Variable: return default allocator and file manager url
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
@Transactional
class ServerVariableService {

	/**
	 * Returns the default allocator configured in database
	 * @return default allocator
	 */
    def getDefaultAllocator() {
		return ServerVariable.findByName(UnaCloudConstants.VM_DEFAULT_ALLOCATOR)
    }
	
	/**
	 * Returns the current web server file url
	 * @return file manager url
	 */
	def getUrlFileManager() {
		return ServerVariable.findByName(UnaCloudConstants.WEB_FILE_SERVER_URL).variable
	}
}
