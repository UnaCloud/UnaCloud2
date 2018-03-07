package uniandes.unacloud.agent;

import static uniandes.unacloud.common.utils.UnaCloudConstants.VM_REPO_PATH;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import uniandes.unacloud.agent.execution.AgentManager;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.host.system.OSFactory;
import uniandes.unacloud.agent.net.receive.ClouderClientAttention;
import uniandes.unacloud.agent.net.send.PhysicalMachineStateReporter;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.net.torrent.TorrentClient;
import uniandes.unacloud.agent.platform.PlatformFactory;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Responsible for start UnaCloud Client
 *
 */
public class InitialPoint {
	
	/**
	 * Number of threads to attend messages from server
	 */
	private static final int THREADS = 10;
	
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
    	String repoPath = VariableManager.getInstance().getLocal().getStringVariable(VM_REPO_PATH);
    	if (repoPath == null || dataPath.isEmpty()) {
    		System.out.println(UnaCloudConstants.VM_REPO_PATH + " in local file is empty");
    		System.exit(0);
    	}
    	
    	File repo = new File(repoPath);
    	if (!repo.exists())
    		System.out.println("Making folder repo " + repoPath + " " + repo.mkdirs());

        //Start log    
        try {
    		//Create agent log file
        	PrintStream ps = new PrintStream(new FileOutputStream(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH) + UnaCloudConstants.AGENT_OUT_LOG, true), true){
        	
        		@Override
        		public void println(String x) {
        			super.println(new Date() + " " + x);
        		}
        		@Override
        		public void println(Object x) {
        			super.println(new Date() + " " + x);
        		}
        	};
        	PrintStream psError = new PrintStream(new FileOutputStream(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH) + UnaCloudConstants.AGENT_ERROR_LOG, true), true){
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
			ClouderClientAttention.getInstance(VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.AGENT_PORT), THREADS).start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
        
        AgentManager.sendInitialMessage();
        
        int[] ports = null;
		String portString = VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.TORRENT_CLIENT_PORTS);
		if (portString != null) {
			String [] data = portString.split(",");
			ports = new int[data.length];
			for (int i = 0; i < data.length; i++)
				ports[i] = Integer.parseInt(data[i]);
		}
        
        TorrentClient.getInstance().startService(ports);
  
    }
}

