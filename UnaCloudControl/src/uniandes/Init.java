package uniandes;


/**
 * Start class to initialize all services from UnaCloud Control
 * @author Cesar
 *
 */
public class Init {
	
	/**
	 * Method to initialize all services 
	 * @param args
	 */
	public static void main(String[] args) {		
		try {
			ControlManager.getInstance();					
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}		
		//TODO Task to control report agents
	}

}
