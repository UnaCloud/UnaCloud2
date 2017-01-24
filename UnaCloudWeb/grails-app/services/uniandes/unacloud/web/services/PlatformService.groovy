package uniandes.unacloud.web.services

import uniandes.unacloud.web.domain.Platform;
import grails.transaction.Transactional

/**
 * This service contains all methods to manage Platform: crud platform
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
@Transactional
class PlatformService {

    //-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Creates a new platform in database
	 * @param name platform name 
	 * @param platformVersion platform version
	 */
	
	def create(name, platformVersion, ext,fileExts, cls) {
		new Platform(name:name, platformVersion: platformVersion, mainExtension: ext, filesExtensions: fileExts, classPlatform: cls).save()
    }
	
	/**
	 * Edits platform values
	 * @param platform platform to be edited
	 * @param name platform new name
	 */
	
	def setValues(Platform platform, name, platformVersion, mainExt, filesExt, cls){
		platform.putAt("name", name)
		platform.putAt("platformVersion", platformVersion)
		platform.putAt("mainExtension", mainExt)
		platform.putAt("filesExtensions", filesExt)
		platform.putAt("classPlatform", cls)
	}
	
	/**
	 * Deletes the selected platform
	 * @param platform platform to be deleted
	 */
	
	def deletePlatform(Platform platform){
		platform.delete()
	}
}
