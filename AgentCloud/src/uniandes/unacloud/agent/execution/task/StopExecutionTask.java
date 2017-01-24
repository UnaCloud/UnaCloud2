package uniandes.unacloud.agent.execution.task;

import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.common.com.messages.exeo.ExecutionStopMessage;

/**
 * Task to stop an execution
 * @author CesarF
 *
 */
public class StopExecutionTask implements Runnable{
	
	ExecutionStopMessage stopMessage;
	
	/**
	 * class constructor
	 * @param stopMessage stop message with operation data
	 */
	public StopExecutionTask(ExecutionStopMessage stopMessage) {
		this.stopMessage = stopMessage;
	}
	
	/**
	 * Executes stop message task
	 */
	@Override
	public void run() {
		PersistentExecutionManager.removeExecution((stopMessage).getExecutionId(),false);
	}
}
