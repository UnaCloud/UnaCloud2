package uniandes.unacloud.agent.execution;

import java.util.TimerTask;

/**
 * Responsible for un-deploying a virtual machine execution
 *  @author Clouder
 */
public class Scheduler extends TimerTask {

    private long executionId;
 
    public Scheduler(long executionId) {
		this.executionId = executionId;
	}

	/**
     * Responsible for un-deploying a virtual machine execution in an executionTime
     */
    public void run(){
    	System.out.println("Schedule turning off  "+executionId);
    	PersistentExecutionManager.removeExecution(executionId,true);
    }
}
