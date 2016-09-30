package uniandes.unacloud.agent.execution.task;

import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineStopMessage;

/**
 * Task to stop a virtual machine execution
 * @author CesarF
 *
 */
public class StopVirtualMachineTask implements Runnable{
	
	VirtualMachineStopMessage stopMessage;
	
	/**
	 * class constructor
	 * @param stopMessage stop message with operation data
	 */
	public StopVirtualMachineTask(VirtualMachineStopMessage stopMessage) {
		this.stopMessage = stopMessage;
	}
	
	/**
	 * Executes stop message task
	 */
	@Override
	public void run() {
		PersistentExecutionManager.removeExecution((stopMessage).getVirtualMachineExecutionId(),false);
	}
}
