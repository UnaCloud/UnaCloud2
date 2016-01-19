package uniandes;

import db.PhysicalMachineManager;

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
			//ControlManager.getInstance().startQueueService();
			ControlManager.getInstance().startDatabaseService();
			PhysicalMachineManager.getPhysicalMachineList(new Long[]{1l});
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}		
		//TODO Task to control report agents
	}

}
