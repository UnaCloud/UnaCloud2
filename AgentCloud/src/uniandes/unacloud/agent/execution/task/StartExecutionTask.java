package uniandes.unacloud.agent.execution.task;

import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.Execution;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

/**
 * Task to start an execution
 * @author CesarF
 *
 */
public class StartExecutionTask implements Runnable{
	Execution machineExecution;
	int tipo;
	/**
	 * class constructor
	 * @param machineExecution Execution instance to be started
	 */
	public StartExecutionTask(Execution machineExecution, int tipo) {
		this.machineExecution = machineExecution;
		this.tipo = tipo;
	}
	
	/**
	 * Executes start machine task
	 */
	@Override
	public void run() {
		System.out.println("Start Execution");
		try{
			//get image 
			ImageCopy image=ImageCacheManager.getFreeImageCopy(machineExecution.getImageId(), tipo);
			System.out.println("Get Image");
			machineExecution.setImage(image);
			image.configureAndStart(machineExecution);
			System.out.println("endStartExecution");
		}catch(ExecutionException ex){
			try {
				ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionStateEnum.FAILED,ex.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
