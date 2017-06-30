package uniandes.unacloud.agent.execution.configuration;

import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.ImageCopy;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.net.messages.exeo.ExecutionStartResponse;

/**
 * Responsible to configure execution
 * @author clouder
 *
 */
public final class ExecutionConfigurer extends Thread {
	/**
	 * Execution to be configured
	 */
	Execution machineExecution;
	
	/**
	 * Class constructor
	 * @param machineExecution VM
	 */
	public ExecutionConfigurer(Execution machineExecution) {
		this.machineExecution = machineExecution;
	}
	
	/**
	 * Starts a new thread with configuration
	 * @return response of start process
	 */
	public ExecutionStartResponse startProcess(){
		ExecutionStartResponse resp=new ExecutionStartResponse();
		resp.setState(ExecutionStartResponse.ExecutionState.STARTING);
		resp.setMessage("Starting execution...");
		start();
		return resp;
	}
	/**
	 * Thread run modification. Starts configuration process
	 */
	@Override
	public void run() {
		System.out.println("startExecution");
		try {
			try {
				ImageCopy image=ImageCacheManager.getFreeImageCopy(machineExecution.getImageId());
				machineExecution.setImage(image);
				image.configureAndStart(machineExecution);
			} catch(ExecutionException ex) {
				ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionStateEnum.FAILED,ex.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
