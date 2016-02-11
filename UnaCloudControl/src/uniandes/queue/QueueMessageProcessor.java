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
import unacloud.share.db.DeploymentManager;
import unacloud.share.db.PhysicalMachineManager;
import unacloud.share.db.VirtualImageManager;
import unacloud.share.queue.QueueMessage;
import unacloud.share.queue.QueueReader;
import unacloud.share.entities.DeployedImageEntity;
import unacloud.share.entities.DeploymentEntity;
import unacloud.share.entities.NetInterfaceEntity;
import unacloud.share.entities.PhysicalMachineEntity;
import unacloud.share.entities.VirtualMachineExecutionEntity;
import unacloud.share.entities.VirtualMachineImageEntity;
import unacloud.share.enums.PhysicalMachineStateEnum;
import unacloud.share.enums.TaskEnum;
import unacloud.share.enums.VirtualMachineExecutionStateEnum;
import unacloud.share.enums.VirtualMachineImageEnum;
import uniandes.ControlManager;
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
			VirtualMachineImageEntity image = new VirtualMachineImageEntity(imageId, null, null, VirtualMachineImageEnum.REMOVING_CACHE, null);
			VirtualImageManager.setVirtualMachine(image, ControlManager.getInstance().getDBConnection());
			try {				
				List<PhysicalMachineEntity> machines=PhysicalMachineManager.getAllPhysicalMachine(PhysicalMachineStateEnum.ON, ControlManager.getInstance().getDBConnection());			
				for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
					threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), new ClearImageFromCacheMessage(imageId), new ResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							VirtualMachineImageEntity image = new VirtualMachineImageEntity(imageId, null, null, VirtualMachineImageEnum.AVAILABLE, null);
							VirtualImageManager.setVirtualMachine(image, ControlManager.getInstance().getDBConnection());
						}
						@Override
						public void attendError(String message, Long id) {
							VirtualMachineImageEntity image = new VirtualMachineImageEntity(imageId, null, null, VirtualMachineImageEnum.AVAILABLE, null);
							VirtualImageManager.setVirtualMachine(image, ControlManager.getInstance().getDBConnection());
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, ControlManager.getInstance().getDBConnection());
						}
					}));
				}				
			} catch (Exception e) {
				image.setState(VirtualMachineImageEnum.AVAILABLE);
				VirtualImageManager.setVirtualMachine(image, ControlManager.getInstance().getDBConnection());
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
			List<PhysicalMachineEntity> machines=PhysicalMachineManager.getPhysicalMachineList(ids,PhysicalMachineStateEnum.PROCESSING, ControlManager.getInstance().getDBConnection());			
			for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
				UnaCloudAbstractMessage absMessage = task.equals(TaskEnum.CACHE)?
						new ClearVMCacheMessage():task.equals(TaskEnum.STOP)?
								new StopAgentMessage():new UpdateAgentMessage();
				threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), 
						absMessage, new ResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.ON);
						PhysicalMachineManager.setPhysicalMachine(pm, ControlManager.getInstance().getDBConnection());
					}
					@Override
					public void attendError(String message, Long id) {
						PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm, ControlManager.getInstance().getDBConnection());
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
			DeploymentEntity deploy = DeploymentManager.getDeployment(deploymentId, ControlManager.getInstance().getDBConnection());
			if(deploy!=null){
				for(DeployedImageEntity image :deploy.getImages()){
					for(final VirtualMachineExecutionEntity execution : image.getExecutions()){
						VirtualMachineStartMessage vmsm = new VirtualMachineStartMessage();
						vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
						vmsm.setHostname(execution.getHostName());
						vmsm.setVmCores(execution.getCores());
						vmsm.setVmMemory(execution.getRam());
						vmsm.setVirtualMachineExecutionId(execution.getId());
						vmsm.setVirtualMachineImageId(image.getImage().getId());
						List<VirtualNetInterfaceComponent> interfaces = new ArrayList<VirtualNetInterfaceComponent>();
						for(NetInterfaceEntity interf: execution.getInterfaces())
							interfaces.add(new VirtualNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
						vmsm.setInterfaces(interfaces);						
						List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
						machines.add(execution.getNode());
						threadPool.submit(new MessageSender(machines, 
								vmsm, new ResponseProcessor() {			
							@Override
							public void attendResponse(UnaCloudAbstractResponse response, Long id) {
								Date stopTime = new Date();
								stopTime.setTime(stopTime.getTime()+execution.getTime());
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, new Date(), stopTime, null, VirtualMachineExecutionStateEnum.CONFIGURING, null, "Initializing"), ControlManager.getInstance().getDBConnection());
							}
							@Override
							public void attendError(String message, Long id) {
								PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm, ControlManager.getInstance().getDBConnection());
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null, "Communication error"), ControlManager.getInstance().getDBConnection());
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
			List<VirtualMachineExecutionEntity> executions = DeploymentManager.getExecutions(ids,VirtualMachineExecutionStateEnum.DEPLOYED, ControlManager.getInstance().getDBConnection());
			for(final VirtualMachineExecutionEntity execution: executions){
				VirtualMachineStopMessage vmsm=new VirtualMachineStopMessage();
				vmsm.setVirtualMachineExecutionId(execution.getId());
				List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
				machines.add(execution.getNode());
				threadPool.submit(new MessageSender(machines, 
						vmsm, new ResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FINISHED, null, "Finished by request"), ControlManager.getInstance().getDBConnection());
					}
					@Override
					public void attendError(String message, Long id) {
						PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm, ControlManager.getInstance().getDBConnection());
						DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.RECONNECTING, null, "Losing connection from server"), ControlManager.getInstance().getDBConnection());
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
			List<VirtualMachineExecutionEntity> executions = DeploymentManager.getExecutions(ids,VirtualMachineExecutionStateEnum.QUEQUED, ControlManager.getInstance().getDBConnection());
			for(final VirtualMachineExecutionEntity execution : executions) {
				VirtualMachineStartMessage vmsm = new VirtualMachineStartMessage();
				vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
				vmsm.setHostname(execution.getHostName());
				vmsm.setVmCores(execution.getCores());
				vmsm.setVmMemory(execution.getRam());
				vmsm.setVirtualMachineExecutionId(execution.getId());
				vmsm.setVirtualMachineImageId(deployedImageId);
				List<VirtualNetInterfaceComponent> interfaces = new ArrayList<VirtualNetInterfaceComponent>();
				for(NetInterfaceEntity interf: execution.getInterfaces())
					interfaces.add(new VirtualNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
				vmsm.setInterfaces(interfaces);						
				List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
				machines.add(execution.getNode());
				threadPool.submit(new MessageSender(machines, 
						vmsm, new ResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						Date stopTime = new Date();
						stopTime.setTime(stopTime.getTime()+execution.getTime());
						DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, new Date(), stopTime, null, VirtualMachineExecutionStateEnum.CONFIGURING, null,"Initializing"), ControlManager.getInstance().getDBConnection());
					}
					@Override
					public void attendError(String message, Long id) {
						PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm, ControlManager.getInstance().getDBConnection());
						DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null,"Communication error"), ControlManager.getInstance().getDBConnection());
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
			final VirtualMachineExecutionEntity execution = DeploymentManager.getExecution(executionId, VirtualMachineExecutionStateEnum.REQUEST_COPY, ControlManager.getInstance().getDBConnection());
			if(execution!=null){
				final VirtualMachineImageEntity image = VirtualImageManager.getVirtualMachine(imageId, VirtualMachineImageEnum.COPYING, ControlManager.getInstance().getDBConnection());
				if(image!=null){
					VirtualMachineSaveImageMessage vmsim = new VirtualMachineSaveImageMessage();
					vmsim.setTokenCom(image.getToken());
					vmsim.setImageId(imageId);
					vmsim.setVirtualMachineExecutionId(execution.getId());
					List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
					machines.add(execution.getNode());
					threadPool.submit(new MessageSender(machines, 
							vmsim, new ResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							if(response instanceof VirtualMachineSaveImageResponse && ((VirtualMachineSaveImageResponse)response).getState().equals(VirtualMachineState.COPYNG)){
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.COPYING, null, "Copying to server"), ControlManager.getInstance().getDBConnection());
							}else{
								PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm, ControlManager.getInstance().getDBConnection());
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null, ((InvalidOperationResponse)response).getMessage()), ControlManager.getInstance().getDBConnection());
								VirtualImageManager.deleteVirtualMachineImage(image, ControlManager.getInstance().getDBConnection());
							}
						}
						@Override
						public void attendError(String message, Long id) {
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, ControlManager.getInstance().getDBConnection());
							DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null, "Error copying image"), ControlManager.getInstance().getDBConnection());
							VirtualImageManager.deleteVirtualMachineImage(image, ControlManager.getInstance().getDBConnection());
						}
					}));
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
