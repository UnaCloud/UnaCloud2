
import groovy.sql.Sql
import java.io.FileInputStream;

import sun.security.ssl.HandshakeMessage.Finished;
import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.common.utils.ConfigurationReader
import uniandes.unacloud.common.utils.UnaCloudConstants;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.internal.runners.statements.FailOnTimeout;

import uniandes.unacloud.share.queue.QueueRabbitManager;
import uniandes.unacloud.share.utils.EnvironmentManager;
import uniandes.unacloud.web.domain.UserGroup
import uniandes.unacloud.web.services.UserGroupService
import uniandes.unacloud.web.domain.ExecutionState;
import uniandes.unacloud.web.domain.HardwareProfile;
import uniandes.unacloud.web.domain.Platform;
import uniandes.unacloud.web.domain.IP
import uniandes.unacloud.web.domain.IPPool;
import uniandes.unacloud.web.domain.Laboratory;
import uniandes.unacloud.web.domain.OperatingSystem;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.ServerVariable
import uniandes.unacloud.web.domain.User
import uniandes.unacloud.web.domain.Repository
import uniandes.unacloud.web.services.UserService
import uniandes.unacloud.web.domain.enums.ExternalCloudTypeEnum;
import uniandes.unacloud.web.domain.enums.NetworkQualityEnum;
import uniandes.unacloud.share.enums.ServerVariableProgramEnum;
import uniandes.unacloud.share.enums.ServerVariableTypeEnum;
import uniandes.unacloud.utils.security.HashGenerator;
import uniandes.unacloud.web.queue.QueueTaskerControl;
import uniandes.unacloud.web.queue.QueueTaskerFile;
import uniandes.unacloud.web.pmallocators.AllocatorEnum;

import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;

/**
 * Start APP
 * @author CesarF
 */
class BootStrap {
	
	/**
	 * Representation of user service
	 */
	UserService userService 
	
	/**
	 * Representation of user group service
	 */
	UserGroupService userGroupService
		
	/**
	 * Data source to connect to database for sql commands
	 */
	def dataSource

	/**
	 * Initialize
	 */
	def init = { servletContext ->
		ConfigurationReader reader = new ConfigurationReader(EnvironmentManager.getConfigPath() + UnaCloudConstants.FILE_CONFIG)
		println "***** Create HardwareProfile"
		if (HardwareProfile.count() == 0) {
			new HardwareProfile(name:'small', cores:1, ram:1024).save()
			new HardwareProfile(name:'medium', cores:2, ram:2048).save()
			new HardwareProfile(name:'large', cores:4, ram:4096).save()
			new HardwareProfile(name:'xlarge', cores:6, ram:8192).save()			
		}
		//Create default user in case user list is empty
		println "***** Create User"
		if (User.count() == 0) {
			String randomString = userService.designAPIKey()
			User user = new User(name:'UnaCloud', username:'admin', password:HashGenerator.hashSha256(reader.getStringVariable(UnaCloudConstants.DEFAULT_USER_PASSWORD)), description:'Administrator', apiKey: randomString, registerDate:new Date()).save()
			UserGroup admins  = userGroupService.getAdminGroup();
			admins.users.add(user)
			admins.save()
		}
		//Create operating system in case operating list is empty
		println "***** Create OperatingSystem"
		if (OperatingSystem.count() == 0) {
			new OperatingSystem(name:'Windows 7', configurer:'Windows').save();
			new OperatingSystem(name:'Windows 8', configurer:'Windows').save()
			new OperatingSystem(name:'Windows XP', configurer:'Windows').save()
			new OperatingSystem(name:'Debian 6', configurer:'Debian').save();
			new OperatingSystem(name:'Debian 7', configurer:'Debian').save();
			new OperatingSystem(name:'Debian 8', configurer:'Debian').save();
			new OperatingSystem(name:'Ubuntu 10', configurer:'Ubuntu').save();
			new OperatingSystem(name:'Ubuntu 11', configurer:'Ubuntu').save();
			new OperatingSystem(name:'Scientific Linux', configurer:'ScientificLinux').save();
		}

		println "***** Create ServerVariable"
		if (ServerVariable.count() == 0) {
			//Load variables for web
			new ServerVariable(name:UnaCloudConstants.WEB_SERVER_URL, serverVariableType: ServerVariableTypeEnum.STRING, variable: reader.getStringVariable(UnaCloudConstants.WEB_SERVER_URL), program:ServerVariableProgramEnum.WEB).save()
			new ServerVariable(name:UnaCloudConstants.QUEUE_IP, serverVariableType: ServerVariableTypeEnum.STRING, variable:reader.getStringVariable(UnaCloudConstants.QUEUE_IP), program:ServerVariableProgramEnum.SERVER).save()
			new ServerVariable(name:UnaCloudConstants.QUEUE_PORT, serverVariableType: ServerVariableTypeEnum.INT, variable:reader.getStringVariable(UnaCloudConstants.QUEUE_PORT), program:ServerVariableProgramEnum.SERVER).save()
			new ServerVariable(name:UnaCloudConstants.QUEUE_USER, serverVariableType: ServerVariableTypeEnum.STRING, variable:reader.getStringVariable(UnaCloudConstants.QUEUE_USER), program:ServerVariableProgramEnum.SERVER).save()
			new ServerVariable(name:UnaCloudConstants.QUEUE_PASS, serverVariableType: ServerVariableTypeEnum.STRING, variable:reader.getStringVariable(UnaCloudConstants.QUEUE_PASS), program:ServerVariableProgramEnum.SERVER).save()
			new ServerVariable(name:UnaCloudConstants.AGENT_VERSION, serverVariableType: ServerVariableTypeEnum.STRING, variable: reader.getStringVariable(UnaCloudConstants.AGENT_VERSION), program:ServerVariableProgramEnum.WEB).save()
			new ServerVariable(name:UnaCloudConstants.VM_DEFAULT_ALLOCATOR, serverVariableType: ServerVariableTypeEnum.STRING, variable: AllocatorEnum.ROUND_ROBIN.getName(), isList: true, program:ServerVariableProgramEnum.WEB).save()
			//Load variables for communication
			new ServerVariable(name:UnaCloudConstants.TRANSMISSION_PROTOCOL, serverVariableType: ServerVariableTypeEnum.STRING, variable: TransmissionProtocolEnum.TCP.name(), isList: true, program:ServerVariableProgramEnum.WEB).save()
						
			//Load variables for control
			new ServerVariable(name:UnaCloudConstants.CONTROL_SERVER_IP, serverVariableType: ServerVariableTypeEnum.STRING, variable:reader.getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP), program:ServerVariableProgramEnum.CONTROL, serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.CONTROL_MANAGE_PM_PORT, serverVariableType: ServerVariableTypeEnum.INT, variable:reader.getStringVariable(UnaCloudConstants.CONTROL_MANAGE_PM_PORT), program:ServerVariableProgramEnum.CONTROL, serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.CONTROL_MANAGE_VM_PORT, serverVariableType: ServerVariableTypeEnum.INT, variable:reader.getStringVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT), program:ServerVariableProgramEnum.CONTROL, serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.AGENT_PORT, serverVariableType: ServerVariableTypeEnum.INT, variable:reader.getStringVariable(UnaCloudConstants.AGENT_PORT), program:ServerVariableProgramEnum.CONTROL, serverOnly:false).save()
			
			//Load variables for File Manager		
			new ServerVariable(name:UnaCloudConstants.WEB_FILE_SERVER_URL, serverVariableType: ServerVariableTypeEnum.STRING, variable:reader.getStringVariable(UnaCloudConstants.WEB_FILE_SERVER_URL), program:ServerVariableProgramEnum.FILE_MANAGER).save()	
			new ServerVariable(name:UnaCloudConstants.FILE_SERVER_TORRENT_PORT, serverVariableType: ServerVariableTypeEnum.INT, variable:reader.getStringVariable(UnaCloudConstants.FILE_SERVER_TORRENT_PORT), program:ServerVariableProgramEnum.FILE_MANAGER).save()
			new ServerVariable(name:UnaCloudConstants.FILE_SERVER_PORT, serverVariableType: ServerVariableTypeEnum.INT, variable:reader.getStringVariable(UnaCloudConstants.FILE_SERVER_PORT), program:ServerVariableProgramEnum.FILE_MANAGER, serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.FILE_SERVER_IP, serverVariableType: ServerVariableTypeEnum.STRING, variable:reader.getStringVariable(UnaCloudConstants.FILE_SERVER_IP), program:ServerVariableProgramEnum.FILE_MANAGER, serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.VERSION_MANAGER_PORT, serverVariableType: ServerVariableTypeEnum.INT, variable:reader.getStringVariable(UnaCloudConstants.VERSION_MANAGER_PORT), program:ServerVariableProgramEnum.FILE_MANAGER, serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.TORRENT_CLIENT_PORTS, serverVariableType: ServerVariableTypeEnum.STRING, variable:reader.getStringVariable(UnaCloudConstants.TORRENT_CLIENT_PORTS), program:ServerVariableProgramEnum.FILE_MANAGER, serverOnly:false).save()
		}	
			
		println "***** Create Platform"
		if (Platform.count() == 0) {
			new Platform(name: "VirtualBox 5", mainExtension:".vbox", filesExtensions:'.vdi,.vmdk', platformVersion: "5.*", classPlatform:"VBox5").save()
			new Platform(name: "VirtualBox 4", mainExtension:".vbox", filesExtensions:'.vdi,.vmdk', platformVersion: "4.*", classPlatform:"VBox43").save()
		}
		//new Hypervisor(name: Constants.VM_WARE_WORKSTATION, hypervisorVersion: "10",mainExtension:".vmx",filesExtensions:'.vmdk').save()
		//new Hypervisor(name: Constants.VM_WARE_PLAYER, hypervisorVersion: "10",mainExtension:".vmx",filesExtensions:'.vmdk').save()

		println "***** Create Repository"
		if (Repository.count() == 0) {
			Repository repo = new Repository(name:UnaCloudConstants.MAIN_REPOSITORY, capacity: 20, path: reader.getStringVariable(UnaCloudConstants.MAIN_REPOSITORY))
			repo.save(failOnError:true)
		}
		
		println "***** Start sqlProcesses"		
		sqlProcesses()
		
		println "***** Create graph"
		createGraphState()		
				
		println "***** Start QueueServices"
		QueueRabbitManager queueControl = new QueueRabbitManager(
			ServerVariable.findByName(UnaCloudConstants.QUEUE_USER).variable, 
			ServerVariable.findByName(UnaCloudConstants.QUEUE_PASS).variable,
			ServerVariable.findByName(UnaCloudConstants.QUEUE_IP).variable, 
			Integer.parseInt(ServerVariable.findByName(UnaCloudConstants.QUEUE_PORT).variable), 
			UnaCloudConstants.QUEUE_CONTROL);		
		QueueTaskerControl.setQueueConnection(queueControl)		
		QueueRabbitManager queueFile = new QueueRabbitManager(
			ServerVariable.findByName(UnaCloudConstants.QUEUE_USER).variable, 
			ServerVariable.findByName(UnaCloudConstants.QUEUE_PASS).variable,
			ServerVariable.findByName(UnaCloudConstants.QUEUE_IP).variable, 
			Integer.parseInt(ServerVariable.findByName(UnaCloudConstants.QUEUE_PORT).variable),
			UnaCloudConstants.QUEUE_FILE);		
		QueueTaskerFile.setQueueConnection(queueFile)
		
	}
	
	def sqlProcesses() {
		def sql = new Sql(dataSource)
		//Creates event to control executions
		
		try {
			String drop = "DROP TRIGGER IF EXISTS create_request_events"
			String create = "CREATE TRIGGER " +
							"create_request_events AFTER INSERT ON execution " +
							"FOR EACH ROW BEGIN " +
								"INSERT INTO execution_history (state_id, change_time, execution_id, version, message) " +
								"VALUES (NEW.state_id, CURRENT_TIMESTAMP, NEW.id, 1, NEW.message); " +
							"END;"
			println "EXE: " + drop
			println "EXE: " + create
			sql.execute (drop)
			sql.execute (create)
		} catch(Exception e) {
			e.printStackTrace()
		}
		try {
			String drop = "DROP TRIGGER IF EXISTS save_request_events"		
			String trigger = "CREATE TRIGGER " +
							 "save_request_events BEFORE UPDATE ON execution " +
							 "FOR EACH ROW BEGIN " +
								"IF NEW.state_id <> OLD.state_id THEN " +
									"SELECT CURRENT_TIMESTAMP INTO @current; " +
									"INSERT INTO execution_history (state_id, change_time, execution_id, version, message) VALUES (NEW.state_id, @current, NEW.id, 1, NEW.message); " +
									"SELECT id INTO @failed FROM execution_state WHERE state = \"" + ExecutionStateEnum.FAILED.name() + "\"; " +
									"SELECT id INTO @finished FROM execution_state WHERE state = \"" + ExecutionStateEnum.FINISHED.name() + "\"; " +
									"SELECT id INTO @request_copy FROM execution_state WHERE state = \"" + ExecutionStateEnum.REQUEST_COPY.name() + "\"; " +
									"SELECT id INTO @copying FROM execution_state WHERE state = \"" + ExecutionStateEnum.COPYING.name() + "\"; " +
									"SELECT id INTO @deployed FROM execution_state WHERE state = \"" + ExecutionStateEnum.DEPLOYED.name() + "\"; " +
									"SELECT id INTO @finishing FROM execution_state WHERE state = \"" + ExecutionStateEnum.FINISHING.name() + "\"; " +
									"SELECT id INTO @reconnecting FROM execution_state WHERE state = \"" + ExecutionStateEnum.RECONNECTING.name() + "\"; " +
									"SELECT id INTO @deploying FROM execution_state WHERE state = \"" + ExecutionStateEnum.DEPLOYING.name() + "\"; " +
									//If execution has finished all ips must be released
									"IF NEW.state_id = @failed OR (NEW.state_id = @finished AND OLD.state_id <> @failed) THEN " +
										"UPDATE ip SET state = \"" + IPEnum.AVAILABLE.name() + "\" " +
											"WHERE id IN (SELECT ip_id FROM net_interface WHERE execution_id = NEW.id) " +
												"AND id > 0; " +
									//If copy image to server failed copy should be deleted
									"ELSEIF OLD.state_id = @request_copy AND NEW.state_id = @deployed THEN " +
										"DELETE FROM image WHERE id = OLD.copy_to; " +
									//If copy image to server failed copy should be disable to avoid other fails
									"ELSEIF OLD.state_id = @copying AND NEW.state_id = @failed THEN " +
										"UPDATE image SET state = \"" + ImageEnum.UNAVAILABLE.name() + "\" " +
											"WHERE id = OLD.copy_to; " +
									"END IF; " +
									//If execution start process to finish all stop time must be set
									"IF (OLD.state_id = @reconnecting AND NEW.state_id = @failed) OR (OLD.state_id = @request_copy AND NEW.state_id = @copying) OR (OLD.state_id = @deployed AND NEW.state_id = @finishing) THEN " +
										"SET NEW.stop_time = @current; " +
										"SET NEW.last_report = @current;" +
									//If execution has been started
									"ELSEIF OLD.state_id = @deploying AND NEW.state_id = @deployed THEN " +
										"SET NEW.start_time = @current; " +
										"SET NEW.stop_time = DATE_ADD(@current, INTERVAL NEW.duration/1000 SECOND); "+
										"SET NEW.last_report = @current; " +
									"ELSE " +
										"SET NEW.last_report = @current; " +
									"END IF; " +
								"END IF; " +
							 "END"
			println "EXE: " + drop
			sql.execute (drop)
			println "EXE: " + trigger
			sql.execute (trigger)
		} catch(Exception e) {
			e.printStackTrace()
		}
		try {
			String drop = "DROP PROCEDURE IF EXISTS sp_check_pm"
			String create = "CREATE PROCEDURE sp_check_pm() BEGIN " +
							"UPDATE physical_machine SET state = \"" + PhysicalMachineStateEnum.OFF.name() + "\" , with_user = false " +
							"WHERE CURRENT_TIMESTAMP > DATE_ADD(last_report, INTERVAL 4 MINUTE) " +
							"AND state = \"" + PhysicalMachineStateEnum.ON.name() + "\" AND id > 0; " +
						"END"
			String event = "CREATE EVENT if not exists update_pms ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP DO CALL sp_check_pm"
			println "EXE: " + drop
			sql.execute (drop)
			println "EXE: " + create
			sql.execute (create)
			println "EXE: " + event
			//sql.execute ("DROP EVENT update_pms");
			sql.execute (event)
		} catch(Exception e) {
			e.printStackTrace()
		}
		//sql.execute "update physical_machine set state = \"OFF\", with_user=0;"
		try {
			/*
			 * Events are run by the scheduler, which is not started by default. Using SHOW PROCESSLIST is possible to check whether it is started. If not, run the command
			 * http://stackoverflow.com/questions/16767923/mysql-event-not-working
			 */
			String global = "SET GLOBAL event_scheduler = ON"
			println "EXE: " + global
			sql.execute (global)
		} catch(Exception e) {
			e.printStackTrace()
		}
		sql.close();
		
	}
	
	def createGraphState() {
		println "***** Create ExecutionState"
		def sql = new Sql(dataSource)
		if (ExecutionState.count() == 0) {
			long finished = 1;
			long failed = 2;
			long finishing = 3;
			long copying = 4;
			long deployed = 5;
			long requestCopy = 6;
			long reconnecting = 7;
			long deploying = 8;
			long configuring = 9;
			long transmitting = 10;
			long requested = 11;
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[finished, ExecutionStateEnum.FINISHED.name(), null, null, null, null, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[failed, ExecutionStateEnum.FAILED.name(), finished, null, null, null, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[finishing, ExecutionStateEnum.FINISHING.name(), finished, finished,'User requested finishing instance', 1000 * 60, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[copying, ExecutionStateEnum.COPYING.name(), finished, failed, 'Copying process failed', 1000 * 60 * 20, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[deployed, ExecutionStateEnum.DEPLOYED.name(), finishing, null, 'Execution has not been reported for a few minutes', 1000 * 60 * 4, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[requestCopy, ExecutionStateEnum.REQUEST_COPY.name(), copying, deployed, 'Request copy process failed', 1000 * 60 * 4, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[reconnecting, ExecutionStateEnum.RECONNECTING.name(), deployed, failed, 'Connection lost', 1000 * 60 * 10, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[deploying, ExecutionStateEnum.DEPLOYING.name(), deployed, failed, 'Error in deployment, check agent log', 1000 * 60 * 8, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[configuring, ExecutionStateEnum.CONFIGURING.name(), deploying, failed, 'Configuring process failed in agent', 1000 * 60 * 10, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[transmitting, ExecutionStateEnum.TRANSMITTING.name(), configuring, failed, 'Error transmitting file to agent', 1000 * 60 * 20, null])
			sql.execute ('INSERT INTO execution_state (id, state, next_id, next_control_id, control_message, control_time, next_requested_id, version) values (?, ?, ?, ?, ?, ?, ?, 1)',
				[requested, ExecutionStateEnum.REQUESTED.name(), configuring, failed, 'Task failed: agent does not respond', 1000 * 60 * 3, transmitting])
			sql.execute ('UPDATE execution_state SET next_control_id = ?, next_requested_id = ? WHERE id = ? ',
				[reconnecting, requestCopy, deployed])
			
			def states = ExecutionState.getAll()
			for (ExecutionState state: states) {
				println state.state.name() + "  " + state.next + " - " + state.nextRequested + " - " + state.nextControl + " - " + state.controlTime + " - " + state.controlMessage
			}
		}
		sql.close();
	}
	
	def destroy = {
		
	}
	
	
}
