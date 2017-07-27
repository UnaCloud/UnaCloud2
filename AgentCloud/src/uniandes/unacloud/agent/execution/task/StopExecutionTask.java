package uniandes.unacloud.agent.execution.task;

import uniandes.unacloud.agent.execution.PersistentExecutionManager;
/**
 * Task to stop an execution
 * @author CesarF
 *
 */
public class StopExecutionTask implements Runnable {
	
	private long executionId;
	
	/**
	 * class constructor
	 * @param stopMessage stop message with operation data
	 */
	public StopExecutionTask(long exeid) {
		this.executionId = exeid;
	}
	
	/**
	 * Executes stop message task
	 */
	@Override
	public void run() {
		PersistentExecutionManager.removeExecution(executionId, false);
	}
}
