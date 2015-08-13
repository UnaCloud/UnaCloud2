import back.pmallocators.AllocatorEnum;
import back.services.DatabaseService;
import back.services.VariableManagerService;

import com.losandes.utils.Constants;

import fileManager.DataServerSocket;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.internal.runners.statements.FailOnTimeout;

import unacloud2.ExternalCloudProvider;
import unacloud2.HardwareProfile;
import unacloud2.Hypervisor;
import unacloud2.IP
import unacloud2.IPPool;
import unacloud2.Laboratory;
import unacloud2.OperatingSystem;
import unacloud2.PhysicalMachine;
import unacloud2.ServerVariable
import unacloud2.User
import unacloud2.Repository
import unacloud2.enums.ExternalCloudTypeEnum;
import unacloud2.enums.NetworkQualityEnum;
import unacloud2.enums.PhysicalMachineStateEnum;
import unacloud2.enums.ServerVariableTypeEnum;

class BootStrap {
	DatabaseService databaseService
	VariableManagerService variableManagerService

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
			String charset = (('A'..'Z') + ('0'..'9')).join()
			Integer length = 32
			String randomString = RandomStringUtils.random(length, charset.toCharArray())
			new User(name:'Guest',username:'admin',password:prop.getProperty("admin"), userType: 'Administrator',apiKey: randomString).save()
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
			new Repository(name: "Main Repository", capacity: 20, root: prop.getProperty("repository")).save();
		}
		if(ExternalCloudProvider.count()==0){
			new ExternalCloudProvider(name:'Amazon EC2', endpoint: 'https://ec2.amazonaws.com', type: ExternalCloudTypeEnum.COMPUTING).save()
			new ExternalCloudProvider(name:'Amazon S3', endpoint: 'https://s3.amazonaws.com', type: ExternalCloudTypeEnum.STORAGE).save()
			
		}
		if(ServerVariable.count() ==0){
			new ServerVariable(name:'CLOUDER_SERVER_PORT',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("CLOUDER_SERVER_PORT")).save()
			new ServerVariable(name:'CLOUDER_CLIENT_PORT',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("CLOUDER_CLIENT_PORT")).save()
			new ServerVariable(name:'DATA_SOCKET',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("DATA_SOCKET")).save()
			new ServerVariable(name:'FILE_TRANSFER_SOCKET',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("FILE_TRANSFER_SOCKET")).save()
			new ServerVariable(name:'LOG_SOCKET',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("LOG_SOCKET")).save()			
			new ServerVariable(name:'VERSION_MANAGER_PORT',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("VERSION_MANAGER_PORT")).save()
			new ServerVariable(name:'CLOUDER_SERVER_IP',serverVariableType: ServerVariableTypeEnum.STRING,variable:prop.getProperty("CLOUDER_SERVER_IP")).save()
			new ServerVariable(name:'MONITORING_ENABLE',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("MONITORING_ENABLE"), serverOnly: true).save()
			new ServerVariable(name:'MONITOR_FREQUENCY_CPU',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("MONITOR_FREQUENCY_CPU")).save()
			new ServerVariable(name:'MONITOR_REGISTER_FREQUENCY_CPU',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("MONITOR_REGISTER_FREQUENCY_CPU")).save()
			new ServerVariable(name:'MONITORING_DATABASE_NAME',serverVariableType: ServerVariableTypeEnum.STRING,variable:prop.getProperty("MONITORING_DATABASE_NAME")).save()
			new ServerVariable(name:'MONITORING_DATABASE_PASSWORD',serverVariableType: ServerVariableTypeEnum.STRING,variable:prop.getProperty("MONITORING_DATABASE_PASSWORD")).save()
			new ServerVariable(name:'MONITORING_DATABASE_USER',serverVariableType: ServerVariableTypeEnum.STRING,variable:prop.getProperty("MONITORING_DATABASE_USER")).save()
			new ServerVariable(name:'MONITORING_SERVER_IP',serverVariableType: ServerVariableTypeEnum.STRING,variable: prop.getProperty("MONITORING_SERVER_IP")).save()
			new ServerVariable(name:'MONITORING_SERVER_PORT',serverVariableType: ServerVariableTypeEnum.INT,variable: prop.getProperty("MONITORING_SERVER_PORT")).save()
			new ServerVariable(name:'MONITOR_FREQUENCY_ENERGY',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("MONITOR_FREQUENCY_ENERGY")).save()
			new ServerVariable(name:'MONITOR_REGISTER_FREQUENCY_ENERGY',serverVariableType: ServerVariableTypeEnum.INT,variable:prop.getProperty("MONITOR_REGISTER_FREQUENCY_ENERGY")).save()
			new ServerVariable(name:'AGENT_VERSION',serverVariableType: ServerVariableTypeEnum.STRING,variable: prop.getProperty("AGENT_VERSION")).save()
			new ServerVariable(name:'SERVER_URL',serverVariableType: ServerVariableTypeEnum.STRING,variable: 'http://'+InetAddress.getLocalHost().getHostAddress()+'/'+prop.getProperty("SERVER_URL")).save()
			new ServerVariable(name:'VM_ALLOCATOR_NAME',serverVariableType: ServerVariableTypeEnum.STRING,variable: AllocatorEnum.RANDOM, serverOnly: true).save()
		}
		if(ServerVariable.findByName('EXTERNAL_COMPUTING_ACCOUNT')==null)
			new ServerVariable(name:'EXTERNAL_COMPUTING_ACCOUNT', serverVariableType: ServerVariableTypeEnum.STRING, variable:'None', serverOnly: true).save()
		if(ServerVariable.findByName('EXTERNAL_STORAGE_ACCOUNT')==null)
			new ServerVariable(name:'EXTERNAL_STORAGE_ACCOUNT', serverVariableType: ServerVariableTypeEnum.STRING, variable:'None', serverOnly: true).save()
		
		if(Hypervisor.count() == 0){
			new Hypervisor(name: Constants.VIRTUAL_BOX, hypervisorVersion: "4.3.4").save()
			new Hypervisor(name: Constants.VM_WARE_WORKSTATION, hypervisorVersion: "10").save()
			new Hypervisor(name: Constants.VM_WARE_PLAYER, hypervisorVersion: "10").save()
		}
		//databaseService.initDatabase()
		DataServerSocket.startServices(variableManagerService.getIntValue("DATA_SOCKET"));
		//String applicationPath = request.getSession().getServletContext().getRealPath("")
	}
	def destroy = {
	}
}
