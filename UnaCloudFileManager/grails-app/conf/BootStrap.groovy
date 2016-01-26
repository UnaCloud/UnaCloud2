import uniandes.unacloud.FileManager;

class BootStrap {

    def init = { servletContext ->		
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
