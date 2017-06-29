package uniandes.unacloud.agent;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import uniandes.unacloud.agent.execution.AgentManager;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.net.receive.ClouderClientAttention;
import uniandes.unacloud.agent.net.send.PhysicalMachineStateReporter;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.platform.PlatformFactory;
import uniandes.unacloud.agent.system.OSFactory;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Responsible for start UnaCloud Client
 *
 */
public class InitialPoint {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

    /**
     * Responsible for sorting and starting the Client
     * @param args String array
     * @throws Exception 
     */
    public static void main(String[] args) {      
    
        int mainCase = 0;
        //Validates data path 
        String dataPath = VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH);
    	if (dataPath == null || dataPath.isEmpty()) {
    		System.out.println(UnaCloudConstants.DATA_PATH + " in local file is empty");
    		System.exit(0);
    	}

        //Start log    
        try {
    		//Create agent log file
        	PrintStream ps = new PrintStream(new FileOutputStream(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH) + "unacloud_out.log", true), true){
        	
        		@Override
        		public void println(String x) {
        			super.println(new Date() + " " + x);
        		}
        		@Override
        		public void println(Object x) {
        			super.println(new Date() + " " + x);
        		}
        	};
        	PrintStream psError = new PrintStream(new FileOutputStream(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH) + "unacloud_err.log", true), true){
            	@Override
        		public void println(String x) {
        			super.println(new Date() + " " + x);
        		}
        		@Override
        		public void println(Object x) {
        			super.println(new Date() + " " + x);
        		}
        	};
			System.setOut(ps);
			System.setErr(psError);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        System.out.println("Start configuration");         	
       	
    	{
    		//Validate if the user that is executing agent is system user    		
			try {
				if (OSFactory.getOS().isRunningBySuperUser()) {
					System.err.println("You can't execute the agent as " + OSFactory.getOS().getWhoAmI());
	        		System.exit(0);
	        		return;
	        	}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
				return;
			} 
        	
    	}    
		if (args != null && args.length > 0 && !args[0].matches("[0-9]+"))
			mainCase = Integer.parseInt(args[0]);
	    if (mainCase == UnaCloudConstants.TEST) {
	    	try {
				ServerMessageSender.reportPhyisicalMachine(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
	      	System.exit(0);
	      	return;
	    }      

        try {
        	//Init services
        	//register platforms
        	System.out.println("Register platforms");
        	PlatformFactory.registerplatforms();
        	//load executions in files
        	System.out.println("Load data");
        	PersistentExecutionManager.refreshData();
        	//Start process reporter
        	System.out.println("Start reporter");        	
            PhysicalMachineStateReporter.getInstance().start();            
           //Attend messages from server
			ClouderClientAttention.getInstance().start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
  
    }
}

