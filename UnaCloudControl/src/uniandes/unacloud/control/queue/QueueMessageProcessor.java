package uniandes.unacloud.control.queue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.net.messages.InvalidOperationResponse;
import uniandes.unacloud.common.net.messages.UnaCloudAbstractMessage;
import uniandes.unacloud.common.net.messages.UnaCloudAbstractResponse;
import uniandes.unacloud.common.net.messages.agent.AgentMessage;
import uniandes.unacloud.common.net.messages.agent.ClearImageFromCacheMessage;
import uniandes.unacloud.common.net.messages.agent.ClearVMCacheMessage;
import uniandes.unacloud.common.net.messages.agent.GetDataSpaceMessage;
import uniandes.unacloud.common.net.messages.agent.GetVersionMessage;
import uniandes.unacloud.common.net.messages.agent.InformationResponse;
import uniandes.unacloud.common.net.messages.agent.StopAgentMessage;
import uniandes.unacloud.common.net.messages.agent.UpdateAgentMessage;
import uniandes.unacloud.common.net.messages.exeo.ExecutionSaveImageMessage;
import uniandes.unacloud.common.net.messages.exeo.ExecutionSaveImageResponse;
import uniandes.unacloud.common.net.messages.exeo.ExecutionStartMessage;
import uniandes.unacloud.common.net.messages.exeo.ExecutionStopMessage;
import uniandes.unacloud.common.net.messages.exeo.ImageNetInterfaceComponent;
import uniandes.unacloud.common.net.messages.exeo.ExecutionStartResponse.ExecutionState;
import uniandes.unacloud.common.utils.Time;
import uniandes.unacloud.control.ControlManager;
import uniandes.unacloud.control.net.tcp.sender.MessageSender;
import uniandes.unacloud.control.net.udp.processor.AbstractResponseProcessor;
import uniandes.unacloud.share.db.DeploymentManager;
import uniandes.unacloud.share.db.PhysicalMachineManager;
import uniandes.unacloud.share.db.ImageManager;
import uniandes.unacloud.share.db.entities.DeployedImageEntity;
import uniandes.unacloud.share.db.entities.DeploymentEntity;
import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.db.entities.ImageEntity;
import uniandes.unacloud.share.db.entities.NetInterfaceEntity;
import uniandes.unacloud.share.db.entities.PhysicalMachineEntity;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.share.enums.TaskEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.share.queue.QueueReader;
import uniandes.unacloud.share.queue.messages.MessageAddInstances;
import uniandes.unacloud.share.queue.messages.MessageCreateCopyFromExecution;
import uniandes.unacloud.share.queue.messages.MessageDeployCluster;
import uniandes.unacloud.share.queue.messages.MessageIdOfImage;
import uniandes.unacloud.share.queue.messages.MessageStopExecutions;
import uniandes.unacloud.share.queue.messages.MessageTaskMachines;
import uniandes.unacloud.share.queue.messages.QueueMessage;

/**
 * Class to process each message from queue
 * @author CesarF
 *
 */
public class QueueMessageProcessor implements QueueReader{
	
	/**
	 * Quantity of messages send in each thread
	 */
	private int messagesByThread;
		
	/**
	 * Pool of threads to attend messages
	 */
	private ExecutorService threadPool;
	
	/**
	 * Creates message processor based in a quantity of threads and messages processed by thread
	 * @param threads to run in processor threads > 0
	 * @param messages by thread messages > 0 
	 * @throws Exception in case threads or messages have no valid values
	 */
	public QueueMessageProcessor(int threads, int messages) throws Exception {
		if (threads <= 0 || messages <= 0) 
			throw new Exception("parameters not valid");
		threadPool = Executors.newFixedThreadPool(threads);
		this.messagesByThread = messages;
	}

	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receive message " + message.getMessage());
		switch (message.getType()) {
		case CLEAR_CACHE:
			clearCache(new MessageIdOfImage(message));
			break;
		case SEND_TASK:	
			sendTaskToAgents(new MessageTaskMachines(message));
			break;
		case DEPLOY_CLUSTER:
			doDeploy(new MessageDeployCluster(message));
			break;
		case STOP_DEPLOYS:	
			stopDeploy(new MessageStopExecutions(message), "Finished by request");
			break;
		case ADD_INSTANCES:	
			addInstances(new MessageAddInstances(message));
			break;
		case CREATE_COPY:
			requestCopy(new MessageCreateCopyFromExecution(message));
			break;
		default:
			break;
		}
	}
	
	/**
	 * Receives image id and process request to remove the image from agents cache
	 * @param message
	 */
	private void clearCache(MessageIdOfImage message) {
		
		boolean update = false;
		MessageIdOfImage messageId = (MessageIdOfImage) message;
		final Long imageId =  messageId.getIdImage();
		ImageEntity image = new ImageEntity(imageId, null, null, ImageEnum.REMOVING_CACHE, null);
		List<PhysicalMachineEntity> machines = null;
		try (Connection con = ControlManager.getInstance().getDBConnection();) {
			ImageManager.setImage(image, con);
			machines = PhysicalMachineManager.getAllPhysicalMachine(PhysicalMachineStateEnum.ON, con);	
		} catch (Exception e) {
			e.printStackTrace();
		}
						
		try {				
			
			if (machines.size() > 0) {
				for (int i = 0; i < machines.size() ; i += messagesByThread) {
					threadPool.submit(new MessageSender(machines.subList(i, i + messagesByThread > machines.size() ? machines.size() : i + messagesByThread), new ClearImageFromCacheMessage(imageId), new AbstractResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
								ImageEntity image = new ImageEntity(imageId, null, null, ImageEnum.AVAILABLE, null);
								ImageManager.setImage(image, con2);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void attendError(String message, Long id) {
							try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
								ImageEntity image = new ImageEntity(imageId, null, null, ImageEnum.AVAILABLE, null);
								ImageManager.setImage(image, con2);
								PhysicalMachineEntity pm = new PhysicalMachineEntity(id, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm, con2);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}));
				}	
			} else
				update = true;
						
		} catch (Exception e) {
			e.printStackTrace();
			update = true;
		}
		
		if (update) {
			try (Connection con = ControlManager.getInstance().getDBConnection();) {
				image.setState(ImageEnum.AVAILABLE);
				ImageManager.setImage(image, con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends stop, update o clear cache message to specific list of physical machines
	 * @param messageTask
	 */
	private void sendTaskToAgents(MessageTaskMachines messageTask) {		
			
		final TaskEnum task = messageTask.getTask();
		Long[] ids = messageTask.getIdMachines();
		List<PhysicalMachineEntity> machines = null;
		try (Connection con = ControlManager.getInstance().getDBConnection();) {	
			machines = PhysicalMachineManager.getPhysicalMachineList(ids, PhysicalMachineStateEnum.PROCESSING, con);
		} catch (Exception e) {
			e.printStackTrace();		
		}
		if (machines != null) {
			System.out.println("Sending message to " + machines.size());
			for (int i = 0; i < machines.size() ; i += messagesByThread) {
				UnaCloudAbstractMessage absMessage = getMessage(task);
				threadPool.submit(new MessageSender(machines.subList(i, i + messagesByThread > machines.size() ? machines.size() : i + messagesByThread), 
						absMessage, new AbstractResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
							PhysicalMachineEntity pm = null;
							InformationResponse resp = (InformationResponse) response;
							if (task.equals(TaskEnum.STOP) || task.equals(TaskEnum.UPDATE)) pm = new PhysicalMachineEntity(id, PhysicalMachineStateEnum.OFF);
							else if (task.equals(TaskEnum.DATA_SPACE)) pm = new PhysicalMachineEntity(id, null, null, null, Long.parseLong(resp.getMessage()), PhysicalMachineStateEnum.ON);
							else if (task.equals(TaskEnum.VERSION)) pm = new PhysicalMachineEntity(id, null, null, resp.getMessage(), null, PhysicalMachineStateEnum.ON);
							else pm = new PhysicalMachineEntity(id, PhysicalMachineStateEnum.ON);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					@Override
					public void attendError(String message, Long id) {
						try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}));
			}	
		}
		
	}
	
	/**
	 * Returns kind of object message to be sent to agent
	 * @param task enum 
	 * @return agent message
	 * @throws Exception in case task is not valid
	 */
	private AgentMessage getMessage(TaskEnum task) throws IllegalArgumentException {
		if (task.equals(TaskEnum.UPDATE)) new UpdateAgentMessage();
		if (task.equals(TaskEnum.CACHE)) return new ClearVMCacheMessage();
		if (task.equals(TaskEnum.STOP)) new StopAgentMessage();
		if (task.equals(TaskEnum.DATA_SPACE)) new GetDataSpaceMessage();
		if (task.equals(TaskEnum.VERSION)) new GetVersionMessage();	
		throw new IllegalArgumentException();
	}
	
	/**
	 * Sends message to agents to start deploy in physical machines
	 * @param message
	 */
	private void doDeploy(MessageDeployCluster message){
		
		Long deploymentId =  message.getIdDeployment();
		
		DeploymentEntity deploy = null;
		try (Connection con = ControlManager.getInstance().getDBConnection();) {
			deploy = DeploymentManager.getDeployment(deploymentId, con);
		} catch (Exception e) {
			e.printStackTrace();
		}			
		if (deploy != null) {
			System.out.println("Deploy " + deploy.getId());
			for (DeployedImageEntity image : deploy.getImages()) {
				for (final ExecutionEntity execution : image.getExecutions()) {
					
					ExecutionStartMessage vmsm = new ExecutionStartMessage();
					System.out.println("Execution from " + execution.getStartTime() + " to: "+execution.getStopTime() + " - " + execution.getTimeInHours() + " - " + execution.getTime());
					vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
					vmsm.setHostname(execution.getHostName());
					vmsm.setVmCores(execution.getCores());
					vmsm.setVmMemory(execution.getRam());
					vmsm.setExecutionId(execution.getId());
					vmsm.setImageId(image.getImage().getId());
					
					List<ImageNetInterfaceComponent> interfaces = new ArrayList<ImageNetInterfaceComponent>();
					for (NetInterfaceEntity interf: execution.getInterfaces())
						interfaces.add(new ImageNetInterfaceComponent(interf.getIp(), interf.getNetMask(), interf.getName()));
					vmsm.setInterfaces(interfaces);						
					List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
					machines.add(execution.getNode());
					
					threadPool.submit(new MessageSender(machines, 
							vmsm, new AbstractResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
								Date stopTime = new Date();
								stopTime.setTime(stopTime.getTime() + execution.getTime());
								DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, new Date(), stopTime, null, null, null, "Sent message"), con2);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void attendError(String message, Long id) {
							try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
								PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm, con2);
								DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.FAILED, null, "Communication error " + message), con2);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}));
				}
			}				
		}
	
	}
	
	/**
	 * Method to be used by other classes to stop deployments without use queue
	 * TODO: be careful user is not sent in message
	 * @param executionIds
	 */
	public void remoteStopDeploy(Long[] executionIds) {
		for (int i = 0; i < executionIds.length; i++)
			System.out.println("\t Stop: " + executionIds[i]);
		
		MessageStopExecutions message = new MessageStopExecutions("0", executionIds);
		stopDeploy(message,"Execution is not running in server");
	}
	
	/**
	 * Sends a message to agents to stop an execution
	 * @param message
	 * @param text to be saved in database in case of success
	 */
	private void stopDeploy(MessageStopExecutions message, final String text) {
		
		Long[] ids = message.getIdExecutions();
		
		List<ExecutionEntity> executions = null;
		try (Connection con = ControlManager.getInstance().getDBConnection();) {	
			executions = DeploymentManager.getExecutions(ids, null, false, con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (executions != null) {
			for (final ExecutionEntity execution : executions)
				if(execution.getState().equals(ExecutionStateEnum.FINISHED)
						||execution.getState().equals(ExecutionStateEnum.FINISHING)
							||execution.getState().equals(ExecutionStateEnum.FAILED)) {
					ExecutionStopMessage vmsm=new ExecutionStopMessage();
					vmsm.setExecutionId(execution.getId());
					List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
					machines.add(execution.getNode());
					threadPool.submit(new MessageSender(machines, 
							vmsm, new AbstractResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							if (!execution.getState().equals(ExecutionStateEnum.FAILED)) {
								try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
									DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, new Date(), null, ExecutionStateEnum.FINISHED, null, text), con2);
									DeploymentManager.breakFreeInterfaces(execution.getId(), con2, IPEnum.AVAILABLE);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						@Override
						public void attendError(String message, Long id) {
							if (!execution.getState().equals(ExecutionStateEnum.FAILED)) {
								try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
									PhysicalMachineEntity pm = new PhysicalMachineEntity(id, PhysicalMachineStateEnum.OFF);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
									DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.FINISHED, null, "Connection lost with agent, execution will be removed when it reconnects"), con2);
									DeploymentManager.breakFreeInterfaces(execution.getId(), con2, IPEnum.AVAILABLE);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}));
				}
		}			
		
	}
	
	/**
	 * Sends message to agents to add physical machines 
	 * @param message
	 */
	private void addInstances(MessageAddInstances message){
		
		Long imageId = message.getIdImage();
		Long[] ids = message.getIdExecutions();
		
		List<ExecutionEntity> executions = null;
		try (Connection con = ControlManager.getInstance().getDBConnection();) {	
			executions = DeploymentManager.getExecutions(ids, ExecutionStateEnum.QUEUED, true, con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (executions != null) {
			for (final ExecutionEntity execution : executions) {
				ExecutionStartMessage vmsm = new ExecutionStartMessage();
				vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
				vmsm.setHostname(execution.getHostName());
				vmsm.setVmCores(execution.getCores());
				vmsm.setVmMemory(execution.getRam());
				vmsm.setExecutionId(execution.getId());
				vmsm.setImageId(imageId);
				List<ImageNetInterfaceComponent> interfaces = new ArrayList<ImageNetInterfaceComponent>();
				for(NetInterfaceEntity interf: execution.getInterfaces())
					interfaces.add(new ImageNetInterfaceComponent(interf.getIp(), interf.getNetMask(), interf.getName()));
				vmsm.setInterfaces(interfaces);						
				List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
				machines.add(execution.getNode());
				threadPool.submit(new MessageSender(machines, 
						vmsm, new AbstractResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
							Date stopTime = new Date();
							stopTime.setTime(stopTime.getTime() + execution.getTime());
							DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, new Date(), stopTime, null, ExecutionStateEnum.CONFIGURING, null, "Initializing"), con2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					@Override
					public void attendError(String message, Long id) {
						try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
							DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.FAILED, null, "Communication error " + message), con2);
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}));
			}
		}		
	}
	
	/**
	 * Sends a message to one agent to request send a current execution to server
	 * @param message
	 */
	private void requestCopy(MessageCreateCopyFromExecution message){
		
		Long executionId = message.getIdExecution();
		Long newImageId = message.getIdImage();
		Long oldImageId = message.getIdPastImage();
		
		ExecutionEntity execution = null;
		ImageEntity image = null;
		try (Connection con = ControlManager.getInstance().getDBConnection();) {	
			execution = DeploymentManager.getExecution(executionId, ExecutionStateEnum.REQUEST_COPY, con);
			image = ImageManager.getImage(newImageId, ImageEnum.COPYING, con);
		} catch (Exception e) {
			e.printStackTrace();
		}			
		
		if (execution != null && image != null) {
			ExecutionSaveImageMessage vmsim = new ExecutionSaveImageMessage();
			vmsim.setTokenCom(image.getToken());
			vmsim.setImageId(oldImageId);
			vmsim.setExecutionId(execution.getId());
			List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
			machines.add(execution.getNode());
			final ExecutionEntity exe = execution;
			final ImageEntity img = image;
			threadPool.submit(new MessageSender(machines, 
					vmsim, new AbstractResponseProcessor() {			
				@Override
				public void attendResponse(UnaCloudAbstractResponse response, Long id) {
					try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
						if (response instanceof ExecutionSaveImageResponse) {
							if (((ExecutionSaveImageResponse)response).getState().equals(ExecutionState.COPYNG)) {
								DeploymentManager.setExecution(new ExecutionEntity(exe.getId(), 0, 0, null, null, null, ExecutionStateEnum.COPYING, null, null), con2);
							} else {
								DeploymentManager.setExecution(new ExecutionEntity(exe.getId(), 0, 0, null, null, null, ExecutionStateEnum.DEPLOYED, null, ((ExecutionSaveImageResponse)response).getMessage()), con2);
								ImageManager.deleteImage(img, con2);
							}
						} else {
							DeploymentManager.setExecution(new ExecutionEntity(exe.getId(), 0, 0, null, null, null, ExecutionStateEnum.DEPLOYED, null, ((InvalidOperationResponse)response).getMessage()), con2);
							ImageManager.deleteImage(img, con2);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				public void attendError(String message, Long id) {
					try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
						PhysicalMachineEntity pm = new PhysicalMachineEntity(id, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm,con2);
						DeploymentManager.setExecution(new ExecutionEntity(exe.getId(), 0, 0, null, null, null, ExecutionStateEnum.DEPLOYED, null, "Error copying image " + message), con2);
						ImageManager.deleteImage(img, con2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}));
		}	
	}
}
