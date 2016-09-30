package uniandes.unacloud.agent.execution.configuration;

import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.exceptions.VirtualMachineExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.Execution;
import uniandes.unacloud.common.com.messages.vmo.VirtualMachineStartResponse;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

/**
 * Responsible to configure virtual machine
 * @author clouder
 *
 */
public final class ExecutionConfigurer extends Thread{
	/**
	 * VM to be configured
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
	public VirtualMachineStartResponse startProcess(){
		VirtualMachineStartResponse resp=new VirtualMachineStartResponse();
		resp.setState(VirtualMachineStartResponse.VirtualMachineState.STARTING);
		resp.setMessage("Starting virtual machine...");
		start();
		return resp;
	}
	/**
	 * Thread run modification. Starts configuration process
	 */
	@Override
	public void run() {
		System.out.println("startVirtualMachine");
		try {
			try{
				ImageCopy image=ImageCacheManager.getFreeImageCopy(machineExecution.getImageId());
				machineExecution.setImage(image);
				image.configureAndStart(machineExecution);
			}catch(VirtualMachineExecutionException ex){
				ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), ExecutionStateEnum.FAILED,ex.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
