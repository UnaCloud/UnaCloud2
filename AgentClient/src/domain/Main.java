package domain;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import reportManager.PhysicalMachineStateReporter;
import reportManager.ServerMessageSender;

import com.losandes.utils.OperatingSystem;
import com.losandes.utils.UnaCloudConstants;

import hypervisorManager.HypervisorFactory;
import virtualMachineManager.PersistentExecutionManager;
import communication.receive.ClouderClientAttention;

/**
 * Responsible for starting the Clouder Client
 *
 */
public class Main {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

    /**
     * Responsible for sorting and starting the Clouder Client
     * @param 
     * @throws Exception 
     */
    public static void main(String[] args){      	
    	
        int mainCase = 0;
        if (args != null && args.length>0 && !args[0].matches("[0-9]+"))mainCase = Integer.parseInt(args[0]);
        if(mainCase==UnaCloudConstants.TEST){
        	try {
				ServerMessageSender.reportPhyisicalMachine(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	System.exit(0);
        	return;
        }
//    	{
//    		//Validate if the user that is executing agent is system user
//    		String user=OperatingSystem.getWhoAmI();
//        	if(user!=null&&!user.toLowerCase().contains("system")){
//        		System.out.println("You can't execute the agent as "+user);
//        		System.exit(0);
//        		return;
//        	}
//    	}
    	//Validates data path
    	String dataPath = VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH);
    	if(dataPath==null||dataPath.isEmpty()){
    		System.out.println(UnaCloudConstants.DATA_PATH+" in local file is empty");
    		System.exit(0);
    	}
    	//Start log 
//    	try {
//    		//Create agent log file
//        	PrintStream ps=new PrintStream(new FileOutputStream(VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH)+"logClient.txt",true),true){
//        		@Override
//        		public void println(String x) {
//        			super.println(new Date()+" "+x);
//        		}
//        		@Override
//        		public void println(Object x) {
//        			super.println(new Date()+" "+x);
//        		}
//        	};
//			System.setOut(ps);
//			System.setErr(ps);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
    	//Init services
    	//register hypervisors
    	System.out.println("Register hypervisors");
    	HypervisorFactory.registerHypervisors();
    	//load executions in files
    	System.out.println("Load data");
        PersistentExecutionManager.refreshData();
    	System.out.println("Start reporter");
        PhysicalMachineStateReporter.getInstance().start();     
        //Attend messages from server
        try {
			ClouderClientAttention.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
    }
}

