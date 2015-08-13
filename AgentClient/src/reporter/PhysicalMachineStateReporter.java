package reporter;

/**
 * Class responsible for report this physical machine status. Every 30 seconds this class sends a keep alive message to UnaCloud server.
 * @author Clouder
 */
public class PhysicalMachineStateReporter extends Thread{

	private int REPORT_DELAY=60000;
	//private int REPORT_FAIL_LIMIT=5;

	private static PhysicalMachineStateReporter instance;
	public static synchronized PhysicalMachineStateReporter getInstance(){
		if(instance==null)instance=new PhysicalMachineStateReporter();
		return instance;
	}
    /**
     * Constructs a physical machine reporter
     * @param id Id to be used to report this physical machine, It corresponds to the physical machine name
     * @param sleep How much should the reporter wait between reports
     */
    private PhysicalMachineStateReporter(){}

    /**
     * Method that is used to start the reporting thread
     */
    @Override
    public void run() {
       System.out.println("PhysicalMachineStateReporter");
       while(true){
           try{
               PhysicalMachineState.reportPhyisicalMachineUserLogin();
           }catch(Exception sce){
        	   sce.printStackTrace();
           }
           try{
               sleep(REPORT_DELAY);
           }catch(Exception e){
        	   e.printStackTrace();
        	   break;
           }
       }
    }


}

