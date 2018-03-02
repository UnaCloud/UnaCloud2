package uniandes.unacloud.control.queue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.net.tcp.TCPMultipleSender;
import uniandes.unacloud.common.net.tcp.TCPResponseProcessor;
import uniandes.unacloud.common.net.tcp.message.AgentMessage;
import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.common.net.tcp.message.agent.ClearImageFromCacheMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionSaveImageMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ExecutionStartMessage;
import uniandes.unacloud.common.net.tcp.message.exe.ImageNetInterfaceComponent;
import uniandes.unacloud.common.utils.Time;
import uniandes.unacloud.control.ControlManager;
import uniandes.unacloud.share.db.DeploymentManager;
import uniandes.unacloud.share.db.ExecutionManager;
import uniandes.unacloud.share.db.PhysicalMachineManager;
import uniandes.unacloud.share.db.ImageManager;
import uniandes.unacloud.share.db.entities.DeployedImageEntity;
import uniandes.unacloud.share.db.entities.DeploymentEntity;
import uniandes.unacloud.share.db.entities.ExecutionEntity;
import uniandes.unacloud.share.db.entities.ImageEntity;
import uniandes.unacloud.share.db.entities.NetInterfaceEntity;
import uniandes.unacloud.share.db.entities.PhysicalMachineEntity;
import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
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
public class QueueMessageProcessor implements QueueReader {
	
	/**
	 * Quantity of messages send in each thread
	 */
	private final int messagesByThread;
		
	/**
	 * Pool of threads to attend messages
	 */
	private Executor threadPool;
	
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
			removeImageFromCache(new MessageIdOfImage(message));
			break;
		case CLEAR_CACHE_UPDATE:
			removeImageFromCacheAndUpdate(new MessageIdOfImage(message));
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
	private void removeImageFromCache(MessageIdOfImage message) {
		Long imageId =  message.getIdImage();
		List<PhysicalMachineEntity> machines = null;
		try (Connection con = ControlManager.getInstance().getDBConnection();) {
			machines = PhysicalMachineManager.getAllPhysicalMachine(PhysicalMachineStateEnum.ON, con);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {			
			if (machines.size() > 0) {
				
				List<UnaCloudMessage> messageList = new ArrayList<UnaCloudMessage>();
				for (int i = 0, j = 1; i < machines.size() ; i++, j++) {
					messageList.add(new ClearImageFromCacheMessage(machines.get(i).getIp(), ControlManager.getInstance().getAgentPort(), null, imageId, machines.get(i).getId()));
					
					if (j >= messagesByThread || i == machines.size()-1) {
						threadPool.execute(new TCPMultipleSender(messageList, new TCPResponseProcessor() {
							
							@Override
							public void attendResponse(Object response, Object message) {
								
							}
							
							@Override
							public void attendError(Object error, String message) {
								ClearImageFromCacheMessage mss = (ClearImageFromCacheMessage) error;
								try (Connection con2 = ControlManager.getInstance().getDBConnection()) {									
									PhysicalMachineEntity pm = new PhysicalMachineEntity(mss.getImageId(), PhysicalMachineStateEnum.OFF);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
								} catch (Exception e) {
									e.printStackTrace();
								}								
							}
						}));
						j = 0;
						messageList = new ArrayList<UnaCloudMessage>();
					}					
				}					
			} 
						
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Receives image id and process request to remove the image from agents cache, 
	 * This method changes image state to REMOVING_CACHE and after process to AVAILABLE
	 * @param message
	 */
	private void removeImageFromCacheAndUpdate(MessageIdOfImage message) {
		ImageEntity image = new ImageEntity(message.getIdImage(), null, null, ImageEnum.REMOVING_CACHE, null);		
		try (Connection con = ControlManager.getInstance().getDBConnection();) {
			ImageManager.setImage(image, con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		removeImageFromCache(message);
		try (Connection con = ControlManager.getInstance().getDBConnection();) {
			image.setState(ImageEnum.AVAILABLE);
			ImageManager.setImage(image, con);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends stop, update o clear cache message to specific list of physical machines
	 * @param messageTask
	 */
	private void sendTaskToAgents(MessageTaskMachines messageTask) {		
			
		int task = messageTask.getTask().getId();
		Long[] ids = messageTask.getIdMachines();
		List<PhysicalMachineEntity> machines = null;
		try (Connection con = ControlManager.getInstance().getDBConnection();) {	
			machines = PhysicalMachineManager.getPhysicalMachineList(ids, PhysicalMachineStateEnum.PROCESSING, con);
		} catch (Exception e) {
			e.printStackTrace();		
		}
		if (machines != null) {
			try {
				System.out.println("Send message to " + machines.size());
				List<UnaCloudMessage> messageList = new ArrayList<UnaCloudMessage>();
				for (int i = 0, j = 1; i < machines.size() ; i++, j++) {
					messageList.add(new AgentMessage(machines.get(i).getIp(), ControlManager.getInstance().getAgentPort(), null, task, machines.get(i).getId()));
					
					if (j >= messagesByThread || i == machines.size()-1) {
						System.out.println("\tReady for " + messageList.size());
						threadPool.execute(new TCPMultipleSender(messageList, new TCPResponseProcessor() {
							
							@Override
							public void attendResponse(Object response, Object message) {
								AgentMessage mss = (AgentMessage) message;
								UnaCloudResponse resp = (UnaCloudResponse) response;
								try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
									PhysicalMachineEntity pm = null;
									System.out.println("Message process: " + mss.getTask() + "  -  " + mss.getPmId() + " - " + resp.getMessage());
									if (mss.getTask() == AgentMessage.STOP_CLIENT || mss.getTask() == AgentMessage.UPDATE_OPERATION) 
										pm = new PhysicalMachineEntity(mss.getPmId(), PhysicalMachineStateEnum.OFF);
									else if (mss.getTask() == AgentMessage.GET_DATA_SPACE) 
										pm = new PhysicalMachineEntity(mss.getPmId(), null, null, null, Long.parseLong(resp.getMessage()), PhysicalMachineStateEnum.ON, null, null);
									else if (mss.getTask() == AgentMessage.GET_VERSION) 
										pm = new PhysicalMachineEntity(mss.getPmId(), null, null, resp.getMessage(), null, PhysicalMachineStateEnum.ON, null, null);
									else 
										pm = new PhysicalMachineEntity(mss.getPmId(), PhysicalMachineStateEnum.ON);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							@Override
							public void attendError(Object error, String message) {
								AgentMessage mss = (AgentMessage) error;
								System.out.println("Error: " + message + " - " + mss);
								try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
									PhysicalMachineEntity pm = new PhysicalMachineEntity(mss.getPmId(), PhysicalMachineStateEnum.OFF);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
								} catch (Exception e) {
									e.printStackTrace();
								}							
							}
						}));
						j = 0;
						messageList = new ArrayList<UnaCloudMessage>();
					}		
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
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
			try {
				System.out.println("Deploy " + deploy.getId());
				for (DeployedImageEntity image : deploy.getImages()) {
					List<UnaCloudMessage> messageList = new ArrayList<UnaCloudMessage>();
					for (int i = 0, j = 1; i < image.getExecutions().size() ; i++, j++) {
						List<ImageNetInterfaceComponent> interfaces = new ArrayList<ImageNetInterfaceComponent>();
						ExecutionEntity execution = image.getExecutions().get(i);
						for (NetInterfaceEntity interf: execution.getInterfaces())
							interfaces.add(new ImageNetInterfaceComponent(interf.getIp(), interf.getNetMask(), interf.getName()));
						ExecutionStartMessage vmsm = new ExecutionStartMessage(
								execution.getNode().getIp(), 
								ControlManager.getInstance().getAgentPort(), 
								null, 
								execution.getId(), 
								execution.getNode().getId(),
								image.getImage().getId(), 
								execution.getCores(), 
								execution.getRam(), 
								new Time(execution.getTimeInHours(), TimeUnit.HOURS), 
								execution.getHostName(),
								message.getTypeTransmission(),
								interfaces);
						System.out.println("Execution " + execution.getId() + " - " + execution.getTimeInHours() + " - " + execution.getDuration());
						
						messageList.add(vmsm);
						if (j >= messagesByThread || i == image.getExecutions().size()-1) {
													
							threadPool.execute(new TCPMultipleSender(messageList, new TCPResponseProcessor() {
								
								@Override
								public void attendResponse(Object response, Object message) {
									UnaCloudResponse resp = (UnaCloudResponse) response;
									System.out.println("Response: " + resp);
								}
								
								@Override
								public void attendError(Object error, String message) {
									ExecutionStartMessage mss = (ExecutionStartMessage) error;
									System.out.println("Error: " + error + " - " + message);
									try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
										PhysicalMachineEntity pm = new PhysicalMachineEntity(mss.getPmId(), null, null, PhysicalMachineStateEnum.OFF, null);
										PhysicalMachineManager.setPhysicalMachine(pm, con2);
										ExecutionEntity exe = new ExecutionEntity(mss.getExecutionId(), 0, 0, null, null, ExecutionProcessEnum.FAIL, null, "Communication error " + message);
										ExecutionManager.updateExecution(exe, ExecutionStateEnum.REQUESTED, con2);
									} catch (Exception e) {
										e.printStackTrace();
									}						
								}
							}));
							j = 0;
							messageList = new ArrayList<UnaCloudMessage>();
						}
					}					
				}		
			} catch (Exception e) {
				e.printStackTrace();
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
		stopDeploy(message, "Execution is not running in server");
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
			ExecutionStateEnum[] states = new ExecutionStateEnum[] {
					ExecutionStateEnum.FINISHED,
					ExecutionStateEnum.FINISHING,
					ExecutionStateEnum.FAILED
			};
			executions = ExecutionManager.getExecutions(ids, false, states, con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (executions != null) {
			try {
				List<UnaCloudMessage> messageList = new ArrayList<UnaCloudMessage>();
				for (int i = 0, j = 1; i < executions.size() ; i++, j++) {
					ExecutionEntity execution = executions.get(i);
					ImageOperationMessage vmsm  = new ImageOperationMessage(
							execution.getNode().getIp(), 
							ControlManager.getInstance().getAgentPort(), 
							null,  
							ImageOperationMessage.VM_STOP, 
							execution.getNode().getId(),
							execution.getId());
					messageList.add(vmsm);
					if (j >= messagesByThread || i == executions.size()-1) {
						threadPool.execute(new TCPMultipleSender(messageList, new TCPResponseProcessor() {
							
							@Override
							public void attendResponse(Object response, Object message) {
								ImageOperationMessage mss = (ImageOperationMessage) message;
								try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
									ExecutionEntity exe = new ExecutionEntity(mss.getExecutionId(), 0, 0, null, null, ExecutionProcessEnum.SUCCESS, null, text);
									ExecutionManager.updateExecution(exe, null, con2);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							@Override
							public void attendError(Object error, String message) {
								ImageOperationMessage mss = (ImageOperationMessage) error;
								try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
									PhysicalMachineEntity pm = new PhysicalMachineEntity(mss.getPmId(), PhysicalMachineStateEnum.OFF);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
									ExecutionEntity exe = new ExecutionEntity(mss.getExecutionId(), 0, 0, null, null, ExecutionProcessEnum.SUCCESS, null, "Connection lost with agent, execution will be removed when it reconnects");
									ExecutionManager.updateExecution(exe, null, con2);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}));
						j = 0;
						messageList = new ArrayList<UnaCloudMessage>();
					}				
				}
			} catch (Exception e) {
				e.printStackTrace();
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
			ExecutionStateEnum[] states = new ExecutionStateEnum[] {
					ExecutionStateEnum.REQUESTED
			};
			executions = ExecutionManager.getExecutions(ids, true, states, con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (executions != null) {
			try {
				List<UnaCloudMessage> messageList = new ArrayList<UnaCloudMessage>();
				for (int i = 0, j = 1; i < executions.size() ; i++, j++) {
					ExecutionEntity execution = executions.get(i);
					List<ImageNetInterfaceComponent> interfaces = new ArrayList<ImageNetInterfaceComponent>();
					for (NetInterfaceEntity interf: execution.getInterfaces())
						interfaces.add(new ImageNetInterfaceComponent(interf.getIp(), interf.getNetMask(), interf.getName()));
					ExecutionStartMessage vmsm = new ExecutionStartMessage(
							execution.getNode().getIp(), 
							ControlManager.getInstance().getAgentPort(), 
							null, 
							execution.getId(), 
							execution.getNode().getId(),
							imageId, 
							execution.getCores(), 
							execution.getRam(), 
							new Time(execution.getTimeInHours(), TimeUnit.HOURS), 
							execution.getHostName(),
							message.getTypeTransmission(),
							interfaces);
					System.out.println("Execution from " + execution.getTimeInHours() + " - " + execution.getDuration());
					
					messageList.add(vmsm);
					
					if (j >= messagesByThread || i == executions.size()-1) {
												
						threadPool.execute(new TCPMultipleSender(messageList, new TCPResponseProcessor() {
							
							@Override
							public void attendResponse(Object response, Object message) {
								//ExecutionStartMessage mss = (ExecutionStartMessage) message;
								
							}
							
							@Override
							public void attendError(Object error, String message) {
								ExecutionStartMessage mss = (ExecutionStartMessage) error;
								try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
									PhysicalMachineEntity pm = new PhysicalMachineEntity(mss.getPmId(), null, null, PhysicalMachineStateEnum.OFF, null);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
									ExecutionEntity exe = new ExecutionEntity(mss.getExecutionId(), 0, 0, null, null, ExecutionProcessEnum.FAIL, null, "Communication error " + message);
									ExecutionManager.updateExecution(exe, ExecutionStateEnum.REQUESTED, con2);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}));
						j = 0;
						messageList = new ArrayList<UnaCloudMessage>();
					}
				}	
			} catch (Exception e) {
				e.printStackTrace();
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
			execution = ExecutionManager.getExecution(executionId, ExecutionStateEnum.REQUEST_COPY, con);
			image = ImageManager.getImage(newImageId, ImageEnum.COPYING, con);
		} catch (Exception e) {
			e.printStackTrace();
		}			
		
		if (execution != null && image != null) {
			try {
				ExecutionSaveImageMessage vmsim = new ExecutionSaveImageMessage(
						execution.getNode().getIp(), 
						ControlManager.getInstance().getAgentPort(), 
						null, 
						executionId, 
						execution.getNode().getId(),
						image.getToken(), 
						oldImageId);
				List<UnaCloudMessage> messageList = new ArrayList<UnaCloudMessage>();
				messageList.add(vmsim);
				
				threadPool.execute(new TCPMultipleSender(messageList, new TCPResponseProcessor() {
					
					@Override
					public void attendResponse(Object response, Object message) {
						ExecutionSaveImageMessage mss = (ExecutionSaveImageMessage) message;
						try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
							UnaCloudResponse resp = (UnaCloudResponse) response;
							ExecutionEntity exe = new ExecutionEntity(mss.getExecutionId(), 0, 0, null, null, resp.getState(), null, "Client: " + resp.getMessage());
							ExecutionManager.updateExecution(exe, ExecutionStateEnum.REQUEST_COPY, con2);							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					@Override
					public void attendError(Object error, String message) {
						ExecutionStartMessage mss = (ExecutionStartMessage) error;
						try (Connection con2 = ControlManager.getInstance().getDBConnection()) {
							PhysicalMachineEntity pm = new PhysicalMachineEntity(mss.getPmId(), PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm,con2);
							ExecutionEntity exe = new ExecutionEntity(mss.getExecutionId(), 0, 0, null, null, ExecutionProcessEnum.FAIL, null, "Error copying image " + message);
							ExecutionManager.updateExecution(exe, ExecutionStateEnum.REQUEST_COPY, con2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}));	
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}	
	}
}
