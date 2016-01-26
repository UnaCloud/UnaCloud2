package uniandes.queue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.losandes.utils.Constants;
import com.losandes.utils.Time;

import communication.UnaCloudAbstractMessage;
import communication.UnaCloudAbstractResponse;
import communication.messages.InvalidOperationResponse;
import communication.messages.ao.ClearImageFromCacheMessage;
import communication.messages.ao.ClearVMCacheMessage;
import communication.messages.ao.StopAgentMessage;
import communication.messages.ao.UpdateAgentMessage;
import communication.messages.vmo.VirtualMachineSaveImageMessage;
import communication.messages.vmo.VirtualMachineSaveImageResponse;
import communication.messages.vmo.VirtualMachineStartMessage;
import communication.messages.vmo.VirtualMachineStartResponse.VirtualMachineState;
import communication.messages.vmo.VirtualMachineStopMessage;
import communication.messages.vmo.VirtualNetInterfaceComponent;
import db.DeploymentManager;
import db.PhysicalMachineManager;
import db.VirtualImageManager;
import queue.QueueMessage;
import queue.QueueReader;
import unacloud.entities.DeployedImage;
import unacloud.entities.Deployment;
import unacloud.entities.NetInterface;
import unacloud.entities.PhysicalMachine;
import unacloud.entities.VirtualMachineExecution;
import unacloud.entities.VirtualMachineImage;
import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.TaskEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.enums.VirtualMachineImageEnum;
import uniandes.communication.MessageSender;
import uniandes.communication.ResponseProcessor;

/**
 * Class to process each message from queue
 * @author Cesar
 *
 */
public class QueueMessageProcessor implements QueueReader{
	
	private ExecutorService threadPool=Executors.newFixedThreadPool(5);

	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receive message "+message.getType());
		switch (message.getType()) {
		case CLEAR_CACHE:	
			clearCache(message);
			break;
		case SEND_TASK:			
			sendTaskToAgents(message);
			break;
		case DEPLOY_CLUSTER:
			doDeploy(message);
			break;
		case STOP_DEPLOYS:	
			stopDeploy(message);
			break;
		case ADD_INSTANCES:		
			addInstances(message);
			break;
		case CREATE_COPY:	
			requestCopy(message);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Get virtual image in queuemessage and process request to remove the image from agents cache
	 * @param message
	 */
	private void clearCache(QueueMessage message){
		try {		
			final Long imageId =  Long.parseLong(message.getMessageParts()[0]);
			VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.REMOVING_CACHE, null);
			VirtualImageManager.setVirtualMachine(image);
			try {				
				List<PhysicalMachine> machines=PhysicalMachineManager.getAllPhysicalMachine(PhysicalMachineStateEnum.ON);			
				for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
					threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), new ClearImageFromCacheMessage(imageId), new ResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.AVAILABLE, null);
							VirtualImageManager.setVirtualMachine(image);
						}
						@Override
						public void attendError(String message, Long id) {
							VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.AVAILABLE, null);
							VirtualImageManager.setVirtualMachine(image);
							PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm);
						}
					}));
				}				
			} catch (Exception e) {
				image.setState(VirtualMachineImageEnum.AVAILABLE);
				VirtualImageManager.setVirtualMachine(image);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Method to send stop, update o clear cache message to specific list of physical machines
	 * @param message
	 */
	private void sendTaskToAgents(QueueMessage message){
		try {
			TaskEnum task = TaskEnum.getEnum(message.getMessageParts()[0]);
			Long[] ids = new Long[message.getMessageParts().length-1];
			for (int i = 1, j=0; i < message.getMessageParts().length; i++, j++) {
				ids[j]=Long.parseLong(message.getMessageParts()[i]);
			}
			List<PhysicalMachine> machines=PhysicalMachineManager.getPhysicalMachineList(ids,PhysicalMachineStateEnum.PROCESSING);			
			for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
				UnaCloudAbstractMessage absMessage = task.equals(TaskEnum.CACHE)?
						new ClearVMCacheMessage():task.equals(TaskEnum.STOP)?
								new StopAgentMessage():new UpdateAgentMessage();
				threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), 
						absMessage, new ResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.ON);
						PhysicalMachineManager.setPhysicalMachine(pm);
					}
					@Override
					public void attendError(String message, Long id) {
						PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm);
					}
				}));
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to send message to agents to start deploy in physical machines
	 * @param message
	 */
	private void doDeploy(QueueMessage message){
		try {
			Long deploymentId =  Long.parseLong(message.getMessageParts()[0]);
			Deployment deploy = DeploymentManager.getDeployment(deploymentId);
			if(deploy!=null){
				for(DeployedImage image :deploy.getImages()){
					for(final VirtualMachineExecution execution : image.getExecutions()){
						VirtualMachineStartMessage vmsm = new VirtualMachineStartMessage();
						vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
						vmsm.setHostname(execution.getHostName());
						vmsm.setVmCores(execution.getCores());
						vmsm.setVmMemory(execution.getRam());
						vmsm.setVirtualMachineExecutionId(execution.getId());
						vmsm.setVirtualMachineImageId(image.getImage().getId());
						List<VirtualNetInterfaceComponent> interfaces = new ArrayList<VirtualNetInterfaceComponent>();
						for(NetInterface interf: execution.getInterfaces())
							interfaces.add(new VirtualNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
						vmsm.setInterfaces(interfaces);						
						List<PhysicalMachine> machines = new ArrayList<PhysicalMachine>();
						machines.add(execution.getNode());
						threadPool.submit(new MessageSender(machines, 
								vmsm, new ResponseProcessor() {			
							@Override
							public void attendResponse(UnaCloudAbstractResponse response, Long id) {
								Date stopTime = new Date();
								stopTime.setTime(stopTime.getTime()+execution.getTime());
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, new Date(), stopTime, null, VirtualMachineExecutionStateEnum.CONFIGURING, null, "Initializing"));
							}
							@Override
							public void attendError(String message, Long id) {
								PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm);
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null, "Communication error"));
							}
						}));
					}
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to send a message to agents to stop a virtual machine execution
	 * @param message
	 */
	private void stopDeploy(QueueMessage message){
		try {
			Long[] ids = new Long[message.getMessageParts().length];
			for (int i = 0; i < message.getMessageParts().length; i++) {
				ids[i]=Long.parseLong(message.getMessageParts()[i]);
			}
			List<VirtualMachineExecution> executions = DeploymentManager.getExecutions(ids,VirtualMachineExecutionStateEnum.DEPLOYED);
			for(final VirtualMachineExecution execution: executions){
				VirtualMachineStopMessage vmsm=new VirtualMachineStopMessage();
				vmsm.setVirtualMachineExecutionId(execution.getId());
				List<PhysicalMachine> machines = new ArrayList<PhysicalMachine>();
				machines.add(execution.getNode());
				threadPool.submit(new MessageSender(machines, 
						vmsm, new ResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FINISHED, null, "Finished by request"));
					}
					@Override
					public void attendError(String message, Long id) {
						PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm);
						DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.RECONNECTING, null, "Losing connection from server"));
					}
				}));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to send message to agents to add physical machines 
	 * @param message
	 */
	private void addInstances(QueueMessage message){
		try {
			Long deployedImageId = Long.parseLong(message.getMessageParts()[0]);
			Long[] ids = new Long[message.getMessageParts().length-1];
			for (int i = 1, j=0; i < message.getMessageParts().length; i++, j++) {
				ids[j]=Long.parseLong(message.getMessageParts()[i]);
			}
			List<VirtualMachineExecution> executions = DeploymentManager.getExecutions(ids,VirtualMachineExecutionStateEnum.QUEQUED);
			for(final VirtualMachineExecution execution : executions) {
				VirtualMachineStartMessage vmsm = new VirtualMachineStartMessage();
				vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
				vmsm.setHostname(execution.getHostName());
				vmsm.setVmCores(execution.getCores());
				vmsm.setVmMemory(execution.getRam());
				vmsm.setVirtualMachineExecutionId(execution.getId());
				vmsm.setVirtualMachineImageId(deployedImageId);
				List<VirtualNetInterfaceComponent> interfaces = new ArrayList<VirtualNetInterfaceComponent>();
				for(NetInterface interf: execution.getInterfaces())
					interfaces.add(new VirtualNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
				vmsm.setInterfaces(interfaces);						
				List<PhysicalMachine> machines = new ArrayList<PhysicalMachine>();
				machines.add(execution.getNode());
				threadPool.submit(new MessageSender(machines, 
						vmsm, new ResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						Date stopTime = new Date();
						stopTime.setTime(stopTime.getTime()+execution.getTime());
						DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, new Date(), stopTime, null, VirtualMachineExecutionStateEnum.CONFIGURING, null,"Initializing"));
					}
					@Override
					public void attendError(String message, Long id) {
						PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm);
						DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null,"Communication error"));
					}
				}));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to send a message to one agent to request send a current virtual execution to server
	 * @param message
	 */
	private void requestCopy(QueueMessage message){
		try {
			Long executionId = Long.parseLong(message.getMessageParts()[0]);
			Long imageId = Long.parseLong(message.getMessageParts()[1]);
			final VirtualMachineExecution execution = DeploymentManager.getExecution(executionId, VirtualMachineExecutionStateEnum.REQUEST_COPY);
			if(execution!=null){
				final VirtualMachineImage image = VirtualImageManager.getVirtualMachine(imageId, VirtualMachineImageEnum.COPYING);
				if(image!=null){
					VirtualMachineSaveImageMessage vmsim = new VirtualMachineSaveImageMessage();
					vmsim.setTokenCom(image.getToken());
					vmsim.setImageId(imageId);
					vmsim.setVirtualMachineExecutionId(execution.getId());
					List<PhysicalMachine> machines = new ArrayList<PhysicalMachine>();
					machines.add(execution.getNode());
					threadPool.submit(new MessageSender(machines, 
							vmsim, new ResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							if(response instanceof VirtualMachineSaveImageResponse && ((VirtualMachineSaveImageResponse)response).getState().equals(VirtualMachineState.COPYNG)){
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.COPYING, null, "Copying to server"));
							}else{
								PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm);
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null, ((InvalidOperationResponse)response).getMessage()));
								VirtualImageManager.deleteVirtualMachineImage(image);
							}
						}
						@Override
						public void attendError(String message, Long id) {
							PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm);
							DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null, "Error copying image"));
							VirtualImageManager.deleteVirtualMachineImage(image);
						}
					}));
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
