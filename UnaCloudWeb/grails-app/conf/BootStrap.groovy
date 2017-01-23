
import java.io.FileInputStream;



import uniandes.unacloud.common.utils.ConfigurationReader
import uniandes.unacloud.common.utils.UnaCloudConstants;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.internal.runners.statements.FailOnTimeout;

import uniandes.unacloud.share.queue.QueueRabbitManager;
import uniandes.unacloud.share.utils.EnvironmentManager;
import uniandes.unacloud.web.domain.ExternalCloudProvider;
import uniandes.unacloud.web.domain.UserGroup
import uniandes.unacloud.web.services.UserGroupService
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
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.share.enums.ServerVariableProgramEnum;
import uniandes.unacloud.share.enums.ServerVariableTypeEnum;
import uniandes.unacloud.web.queue.QueueTaskerControl;
import uniandes.unacloud.web.queue.QueueTaskerFile;
import uniandes.unacloud.web.utils.java.Hasher;
import uniandes.unacloud.web.pmallocators.AllocatorEnum;
import uniandes.unacloud.web.services.init.DatabaseService;

/**
 * Start APP
 * @author CesarF
 */
class BootStrap {
	UserService userService 
	UserGroupService userGroupService
	DatabaseService databaseService

	def init = { servletContext ->
		ConfigurationReader reader = new ConfigurationReader(EnvironmentManager.getConfigPath()+UnaCloudConstants.FILE_CONFIG)
		if(HardwareProfile.count() ==0){
			new HardwareProfile(name:'small', cores:1, ram:1024).save()
			new HardwareProfile(name:'medium', cores:2, ram:2048).save()
			new HardwareProfile(name:'large', cores:4, ram:4096).save()
			new HardwareProfile(name:'xlarge',cores:6, ram:8192).save()			
		}
		//Create default user in case user list is empty
		if(User.count() ==0){
			String randomString = userService.designAPIKey()
			User user = new User(name:'UnaCloud',username:'admin',password:Hasher.hashSha256(reader.getStringVariable(UnaCloudConstants.DEFAULT_USER_PASSWORD)),description:'Administrator',apiKey: randomString, registerDate:new Date()).save()
			UserGroup admins  = userGroupService.getAdminGroup();
			admins.users.add(user)
			admins.save()
		}
		//Create operating system in case operating list is empty
		if(OperatingSystem.count() == 0){
			new OperatingSystem(name:'Windows 7',configurer:'Windows').save();
			new OperatingSystem(name:'Windows 8',configurer:'Windows').save()
			new OperatingSystem(name:'Windows XP',configurer:'Windows').save()
			new OperatingSystem(name:'Debian 6',configurer:'Debian').save();
			new OperatingSystem(name:'Debian 7',configurer:'Debian').save();
			new OperatingSystem(name:'Debian 8',configurer:'Debian').save();
			new OperatingSystem(name:'Ubuntu 10',configurer:'Ubuntu').save();
			new OperatingSystem(name:'Ubuntu 11',configurer:'Ubuntu').save();
			new OperatingSystem(name:'Scientific Linux',configurer:'ScientificLinux').save();
		}

		if(ServerVariable.count() ==0){
			//Load variables for web
			new ServerVariable(name:UnaCloudConstants.WEB_SERVER_URL,serverVariableType: ServerVariableTypeEnum.STRING, variable: reader.getStringVariable(UnaCloudConstants.WEB_SERVER_URL),program:ServerVariableProgramEnum.WEB).save()
			new ServerVariable(name:UnaCloudConstants.QUEUE_IP,serverVariableType: ServerVariableTypeEnum.STRING,variable:reader.getStringVariable(UnaCloudConstants.QUEUE_IP),program:ServerVariableProgramEnum.SERVER).save()
			new ServerVariable(name:UnaCloudConstants.QUEUE_PORT,serverVariableType: ServerVariableTypeEnum.INT,variable:reader.getStringVariable(UnaCloudConstants.QUEUE_PORT),program:ServerVariableProgramEnum.SERVER).save()
			new ServerVariable(name:UnaCloudConstants.QUEUE_USER,serverVariableType: ServerVariableTypeEnum.STRING,variable:reader.getStringVariable(UnaCloudConstants.QUEUE_USER),program:ServerVariableProgramEnum.SERVER).save()
			new ServerVariable(name:UnaCloudConstants.QUEUE_PASS,serverVariableType: ServerVariableTypeEnum.STRING,variable:reader.getStringVariable(UnaCloudConstants.QUEUE_PASS),program:ServerVariableProgramEnum.SERVER).save()
			new ServerVariable(name:UnaCloudConstants.AGENT_VERSION,serverVariableType: ServerVariableTypeEnum.STRING,variable: reader.getStringVariable(UnaCloudConstants.AGENT_VERSION),program:ServerVariableProgramEnum.WEB).save()
			new ServerVariable(name:UnaCloudConstants.VM_DEFAULT_ALLOCATOR,serverVariableType: ServerVariableTypeEnum.STRING,variable: AllocatorEnum.ROUND_ROBIN.getName(), isList: true,program:ServerVariableProgramEnum.WEB).save()

			//Load variables for control
			new ServerVariable(name:UnaCloudConstants.CONTROL_SERVER_IP,serverVariableType: ServerVariableTypeEnum.STRING,variable:reader.getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP),program:ServerVariableProgramEnum.CONTROL,serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.CONTROL_MANAGE_PM_PORT,serverVariableType: ServerVariableTypeEnum.INT,variable:reader.getStringVariable(UnaCloudConstants.CONTROL_MANAGE_PM_PORT),program:ServerVariableProgramEnum.CONTROL,serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.CONTROL_MANAGE_VM_PORT,serverVariableType: ServerVariableTypeEnum.INT,variable:reader.getStringVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT),program:ServerVariableProgramEnum.CONTROL,serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.AGENT_PORT,serverVariableType: ServerVariableTypeEnum.INT,variable:reader.getStringVariable(UnaCloudConstants.AGENT_PORT),program:ServerVariableProgramEnum.CONTROL,serverOnly:false).save()
			
			//Load variables for File Manager		
			new ServerVariable(name:UnaCloudConstants.WEB_FILE_SERVER_URL,serverVariableType: ServerVariableTypeEnum.STRING,variable:reader.getStringVariable(UnaCloudConstants.WEB_FILE_SERVER_URL),program:ServerVariableProgramEnum.FILE_MANAGER).save()	
			new ServerVariable(name:UnaCloudConstants.FILE_SERVER_PORT,serverVariableType: ServerVariableTypeEnum.INT,variable:reader.getStringVariable(UnaCloudConstants.FILE_SERVER_PORT),program:ServerVariableProgramEnum.FILE_MANAGER,serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.FILE_SERVER_IP,serverVariableType: ServerVariableTypeEnum.STRING,variable:reader.getStringVariable(UnaCloudConstants.FILE_SERVER_IP),program:ServerVariableProgramEnum.FILE_MANAGER,serverOnly:false).save()
			new ServerVariable(name:UnaCloudConstants.VERSION_MANAGER_PORT,serverVariableType: ServerVariableTypeEnum.INT,variable:reader.getStringVariable(UnaCloudConstants.VERSION_MANAGER_PORT),program:ServerVariableProgramEnum.FILE_MANAGER,serverOnly:false).save()
			
		}			
		if(Platform.count() == 0){
			new Platform(name: "VirtualBox",mainExtension:".vbox",filesExtensions:'.vdi,.vmdk',platformVersion: "4.3.4",classPlatform:"VirtualBox").save()
		}
		//new Hypervisor(name: Constants.VM_WARE_WORKSTATION, hypervisorVersion: "10",mainExtension:".vmx",filesExtensions:'.vmdk').save()
		//new Hypervisor(name: Constants.VM_WARE_PLAYER, hypervisorVersion: "10",mainExtension:".vmx",filesExtensions:'.vmdk').save()

		if(Repository.count()==0){
			Repository repo = new Repository(name:UnaCloudConstants.MAIN_REPOSITORY, capacity: 20, path: reader.getStringVariable(UnaCloudConstants.MAIN_REPOSITORY))
			repo.save(failOnError:true)
		}

		QueueRabbitManager queueControl = new QueueRabbitManager(ServerVariable.findByName(UnaCloudConstants.QUEUE_USER).variable,ServerVariable.findByName(UnaCloudConstants.QUEUE_PASS).variable,
			ServerVariable.findByName(UnaCloudConstants.QUEUE_IP).variable,Integer.parseInt(ServerVariable.findByName(UnaCloudConstants.QUEUE_PORT).variable),UnaCloudConstants.QUEUE_CONTROL);		
		QueueTaskerControl.setQueueConnection(queueControl)		
		QueueRabbitManager queueFile = new QueueRabbitManager(ServerVariable.findByName(UnaCloudConstants.QUEUE_USER).variable,ServerVariable.findByName(UnaCloudConstants.QUEUE_PASS).variable,
			ServerVariable.findByName(UnaCloudConstants.QUEUE_IP).variable,Integer.parseInt(ServerVariable.findByName(UnaCloudConstants.QUEUE_PORT).variable),UnaCloudConstants.QUEUE_FILE);		
		QueueTaskerFile.setQueueConnection(queueFile)
		
		databaseService.initDatabase()
	}
	def destroy = {
	}
}
