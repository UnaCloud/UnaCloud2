
import com.losandes.utils.Constants;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.internal.runners.statements.FailOnTimeout;

import unacloud.ExternalCloudProvider;
import unacloud.UserGroup
import unacloud.UserGroupService
import unacloud.HardwareProfile;
import unacloud.Hypervisor;
import unacloud.IP
import unacloud.IPPool;
import unacloud.Laboratory;
import unacloud.OperatingSystem;
import unacloud.PhysicalMachine;
import unacloud.ServerVariable
import unacloud.User
import unacloud.Repository
import unacloud.UserService
import unacloud.enums.ExternalCloudTypeEnum;
import unacloud.enums.NetworkQualityEnum;
import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.ServerVariableTypeEnum;
import unacloud.utils.Hasher;
import back.pmallocators.AllocatorEnum;

class BootStrap {
	UserService userService 
	UserGroupService userGroupService

	def init = { servletContext ->
		Properties prop = new Properties();
		String propFileName = "config.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		prop.load(inputStream);
		if(HardwareProfile.count() ==0){
			new HardwareProfile(name:'small', cores:1, ram:1024).save()
			new HardwareProfile(name:'medium', cores:2, ram:2048).save()
			new HardwareProfile(name:'large', cores:4, ram:4096).save()
			new HardwareProfile(name:'xlarge',cores:8, ram:8192).save()			
		}
		if(User.count() ==0){
			String randomString = userService.designAPIKey()
			User user = new User(name:'UnaCloud',username:'admin',password:Hasher.hashSha256(prop.getProperty("admin")),description:'Administrator',apiKey: randomString, registerDate:new Date()).save()
			UserGroup admins  = userGroupService.getAdminGroup();
			admins.users.add(user)
			admins.save()
		}
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

		if(Repository.count()==0){
			new Repository(name: unacloud.Constants.MAIN_REPOSITORY, capacity: 20, root: prop.getProperty("repository")).save();
		}
		if(ServerVariable.count() ==0){
			new ServerVariable(name:'CLOUDER_SERVER_PORT',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("CLOUDER_SERVER_PORT")).save()
			new ServerVariable(name:'CLOUDER_CLIENT_PORT',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("CLOUDER_CLIENT_PORT")).save()
			new ServerVariable(name:'DATA_SOCKET',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("DATA_SOCKET")).save()
			new ServerVariable(name:'FILE_TRANSFER_SOCKET',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("FILE_TRANSFER_SOCKET")).save()
			new ServerVariable(name:'LOG_SOCKET',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("LOG_SOCKET")).save()			
			new ServerVariable(name:'VERSION_MANAGER_PORT',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("VERSION_MANAGER_PORT")).save()
			new ServerVariable(name:'CLOUDER_SERVER_IP',serverVariableType: ServerVariableTypeEnum.STRING,variable:prop.getProperty("CLOUDER_SERVER_IP")).save()
			new ServerVariable(name:'AGENT_VERSION',serverVariableType: ServerVariableTypeEnum.STRING,variable: prop.getProperty("AGENT_VERSION")).save()
			new ServerVariable(name:'SERVER_URL',serverVariableType: ServerVariableTypeEnum.STRING,variable: 'http://'+InetAddress.getLocalHost().getHostAddress()+'/'+prop.getProperty("SERVER_URL")).save()
			new ServerVariable(name:'MONITORING_ENABLE',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("MONITORING_ENABLE"), serverOnly: true).save()
			new ServerVariable(name:'MONITORING_DATABASE_NAME',serverVariableType: ServerVariableTypeEnum.STRING,variable:prop.getProperty("MONITORING_DATABASE_NAME")).save()
			new ServerVariable(name:'MONITORING_DATABASE_PASSWORD',serverVariableType: ServerVariableTypeEnum.STRING,variable:prop.getProperty("MONITORING_DATABASE_PASSWORD")).save()
			new ServerVariable(name:'MONITORING_DATABASE_USER',serverVariableType: ServerVariableTypeEnum.STRING,variable:prop.getProperty("MONITORING_DATABASE_USER")).save()
			new ServerVariable(name:'MONITORING_SERVER_IP',serverVariableType: ServerVariableTypeEnum.STRING,variable: prop.getProperty("MONITORING_SERVER_IP")).save()
			new ServerVariable(name:'MONITORING_SERVER_PORT',serverVariableType: ServerVariableTypeEnum.INT,variable: prop.getProperty("MONITORING_SERVER_PORT")).save()
			new ServerVariable(name:'VM_DEFAULT_ALLOCATOR',serverVariableType: ServerVariableTypeEnum.STRING,variable: AllocatorEnum.RANDOM, serverOnly: true).save()
		}			
		if(Hypervisor.count() == 0){
			new Hypervisor(name: Constants.VIRTUAL_BOX, hypervisorVersion: "4.3.4").save()
			new Hypervisor(name: Constants.VM_WARE_WORKSTATION, hypervisorVersion: "10").save()
			new Hypervisor(name: Constants.VM_WARE_PLAYER, hypervisorVersion: "10").save()
		}
		//databaseService.initDatabase()
		//DataServerSocket.startServices(variableManagerService.getIntValue("DATA_SOCKET"));
	}
	def destroy = {
	}
}
