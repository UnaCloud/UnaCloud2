package back.services

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import communication.UnaCloudAbstractMessage;
import communication.messages.ao.*;
import communication.messages.pmo.PhysicalMachineMonitorMessage
import enums.MonitoringStatus;
import javassist.bytecode.stackmap.BasicBlock.Catch;
import unacloud2.PhysicalMachine;
import unacloud2.ServerVariable;
import unacloud2.VirtualMachineImage;


class AgentService {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of server variable manager service
	 */
	VariableManagerService variableManagerService
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Sends an update message to the given physical machine
	 * @param pm Physical machine to be updated 
	 */
    def updateMachine(PhysicalMachine pm){
		return sendMessage(pm,new UpdateAgentMessage());
	}
	
	/**
	 * Sends a stop message to the given physical machine
	 * @param pm Physical machine which agent will be stopped
	 */
    
	def stopMachine(PhysicalMachine pm){
		return sendMessage(pm,new StopAgentMessage());
	}
	
	/**
	 * Sends a clear image message to all physical machines
	 * @param image Virtual machine image to be deleted from cache
	 */
	
	def clearImageFromCache(VirtualMachineImage image){
		for(pm in PhysicalMachine.all)
		sendMessage(pm,new ClearImageFromCacheMessage(image.id));
	}
	
	/**
	 * Sends a clear cache message to the given physical machine. All images
	 * in the machine cache will be deleted
	 * @param pm Physical machine which cache will be deleted 
	 */
	
	def clearCache(PhysicalMachine pm){
		println "clearCache "+pm.ip
		return sendMessage(pm,new ClearVMCacheMessage());
	}
	
	def updateMonitoring(PhysicalMachine pm, String option, boolean energy, boolean cpu){
		
		PhysicalMachineMonitorMessage pmm = new PhysicalMachineMonitorMessage();
		if(option.equals("start")&&(pm.monitorStatus==MonitoringStatus.OFF||pm.monitorStatusEnergy==MonitoringStatus.OFF)){
		    pmm.operation = PhysicalMachineMonitorMessage.M_START;
		}else if(option.equals("stop")&&
			((pm.monitorStatus==MonitoringStatus.RUNNING||pm.monitorStatus==MonitoringStatus.ERROR)||
				(pm.monitorStatusEnergy==MonitoringStatus.RUNNING||pm.monitorStatusEnergy==MonitoringStatus.ERROR))){
		    pmm.operation = PhysicalMachineMonitorMessage.M_STOP;
		}else if(option.equals("update")){
		    pmm.operation = PhysicalMachineMonitorMessage.M_UPDATE;
			pmm.monitorFrecuencyEnergy = variableManagerService.getIntValue("MONITOR_FREQUENCY_ENERGY")
			pmm.registerFrecuencyEnergy = variableManagerService.getIntValue("MONITOR_REGISTER_FREQUENCY_ENERGY")
			pmm.monitorFrequency = variableManagerService.getIntValue("MONITOR_FREQUENCY_CPU")
			pmm.registerFrequency = variableManagerService.getIntValue("MONITOR_REGISTER_FREQUENCY_CPU")			
		}else if(option.equals("enable")&&(pm.monitorStatus==MonitoringStatus.DISABLE||pm.monitorStatusEnergy==MonitoringStatus.DISABLE)){
			pmm.operation = PhysicalMachineMonitorMessage.M_ENABLE;
		}else return false;		
	    pmm.energy=energy;
		pmm.cpu=cpu
		return sendMessage(pm,pmm);
	}
	
	/**
	 * Sends a generic message to the agent
	 * @param pm Physical machine to which message will be sent
	 * @param message
	 */
	
	def sendMessage(PhysicalMachine pm,UnaCloudAbstractMessage message){
		def res = true;
		String ipAddress=pm.ip.ip;
		try{
			Socket s=new Socket(ipAddress,variableManagerService.getIntValue("CLOUDER_CLIENT_PORT"));
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			oos.writeObject(message);
			oos.flush();
			println ois.readObject();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
			println "Error conectando a "+ipAddress;	
			res= false;		
		}
		try{
			Thread.sleep(500);
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Prepares the agent files and sends them in a zip.
	 * @param outputStream file output stream for download
	 * @param appDir directory where the zip will be stored
	 */
	
	def copyAgentOnStream(OutputStream outputStream,File appDir){
		ZipOutputStream zos=new ZipOutputStream(outputStream);
		//copyFile(zos,"ClientUpdater.jar",new File(appDir,"agentSources/ClientUpdater.jar"),true);
		copyFile(zos,"ClouderClient.jar",new File(appDir,"agentSources/ClouderClient.jar"),true);
		File local = new File(appDir,"agentSources/local");
		if(local.exists())copyFile(zos,"local",local,true);
		zos.putNextEntry(new ZipEntry("vars"));
		PrintWriter pw=new PrintWriter(zos);
		ServerVariable monitor = ServerVariable.findByName("MONITORING_ENABLE");
		boolean monitoring = monitor.variable.equals("1")?true:false;
		for(ServerVariable sv:ServerVariable.all)
		    if(!sv.isServerOnly()){
				if(sv.name.startsWith("MONITOR")){
					if(monitoring)
						pw.println(sv.serverVariableType.type+"."+sv.name+"="+sv.variable);
				}else pw.println(sv.serverVariableType.type+"."+sv.name+"="+sv.variable);
		    }
		pw.flush();
		pw.close();
		zos.closeEntry();
		zos.close();
	}
	
	/**
	 * Prepares the updater files and sends them in a zip.
	 * @param outputStream file output stream for download
	 * @param appDir directory where the zip will be stored
	 */
	 
	def copyUpdaterOnStream(OutputStream outputStream,File appDir){
		ZipOutputStream zos=new ZipOutputStream(outputStream);
		copyFile(zos,"ClientUpdater.jar",new File(appDir,"agentSources/ClientUpdater.jar"),true);
		copyFile(zos,"ClientConfigurer.jar",new File(appDir,"agentSources/ClientConfigurer.jar"),true);
		zos.putNextEntry(new ZipEntry("vars"));
		PrintWriter pw=new PrintWriter(zos);
		ServerVariable monitor = ServerVariable.findByName("MONITORING_ENABLE");
		boolean monitoring = monitor.variable.equals("1")?true:false;
		for(ServerVariable sv:ServerVariable.all)
		    if(!sv.isServerOnly()){
				if(sv.name.startsWith("MONITOR")){
					if(monitoring)
						pw.println(sv.serverVariableType.type+"."+sv.name+"="+sv.variable);
				}else pw.println(sv.serverVariableType.type+"."+sv.name+"="+sv.variable);
		    }
		pw.flush();
		zos.closeEntry();
		zos.close();
	}
	
	/**
	 * Auxiliary method that copies a file in the zip.
	 * @param zos zip output stream in order to copy
	 * @param filePath zip file path
	 * @param f file to be copied
	 * @param tells if the file is in root directory
	 */
	
	private static void copyFile(ZipOutputStream zos,String filePath,File f,boolean root)throws IOException{
		if(f.isDirectory())for(File r:f.listFiles())copyFile(zos,(root?"":(filePath+"/"))+r.getName(),r,false);
		else if(f.isFile()){
			zos.putNextEntry(new ZipEntry(filePath));
			Files.copy(f.toPath(),zos);
			zos.closeEntry();
		}
	}
}
