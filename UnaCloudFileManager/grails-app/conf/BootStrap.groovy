import uniandes.unacloud.common.utils.UnaCloudConstants;
import grails.util.Environment;
import uniandes.unacloud.file.FileManager;
import uniandes.unacloud.file.services.FileService;

/**
 * Initial point in app
 * @author Cesar
 *
 */
class BootStrap {

	FileService fileService
	
    def init = { servletContext ->		
		
		try {
			FileManager.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		fileService.updateProperty();
		
		println 'Path: '+System.getProperty(UnaCloudConstants.ROOT_PATH)
    }
    def destroy = {
    }
}
