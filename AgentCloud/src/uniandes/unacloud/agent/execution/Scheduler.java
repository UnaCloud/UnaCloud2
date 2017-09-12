package uniandes.unacloud.agent.execution;

import java.util.TimerTask;

/**
 * Responsible for un-deploying an execution
 *  @author Clouder
 */
public class Scheduler extends TimerTask {

	/**
	 * Id from execution which is controlled by this timer
	 */
    private long executionId;
 
    /**
     * Creates a new scheduler undeploying task with an executiond Id.
     * @param executionId
     */
    public Scheduler(long executionId) {
		this.executionId = executionId;
	}

	/**
     * Responsible for un-deploying an execution in an executionTime
     */
    public void run() {
    	System.out.println("Schedule turning off  " + executionId);
    	PersistentExecutionManager.removeExecution(executionId, true);
    }
}
