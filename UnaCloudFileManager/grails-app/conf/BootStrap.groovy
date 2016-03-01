import com.losandes.utils.UnaCloudConstants;

import grails.util.Environment;
import uniandes.unacloud.FileManager;

class BootStrap {

    def init = { servletContext ->		
		if(Environment.isDevelopmentMode())	System.setProperty(UnaCloudConstants.ROOT_PATH, "web-app/")
		else System.setProperty(UnaCloudConstants.ROOT_PATH, "")
		try {
			FileManager.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
    }
    def destroy = {
    }
}
