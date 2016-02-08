package unacloud

import grails.transaction.Transactional

import java.util.zip.ZipEntry
import java.nio.file.Files;
import java.util.zip.ZipOutputStream

import unacloud.share.utils.UnaCloudVariables;

@Transactional
class ConfigurationService {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

	/**
	 * Set value in a server variable
	 * @param variable to be modified
	 * @param value to be recorded in variable
	 * @return
	 */
    def setValue(ServerVariable variable, value) {
		variable.putAt("variable", value)
    }
	
	/**
	 * return the current agent version in system
	 * @return
	 */
	def getAgentVersion(){
		return ServerVariable.findByName(UnaCloudVariables.AGENT_VERSION).variable
	}
	
	/**
	 * Set the version of agent increasing the minor number of version server variable
	 * @return
	 */
	def setAgentVersion(){
		ServerVariable agentVersion= ServerVariable.findByName(UnaCloudVariables.AGENT_VERSION)
		int newVerNumber= ((agentVersion.getVariable()-"2.0.") as Integer)+1
		String newVersion=  "2.0."+ newVerNumber
		agentVersion.putAt("variable", newVersion)
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
		ServerVariable monitor = ServerVariable.findByName(UnaCloudVariables.MONITORING_ENABLE);
		boolean monitoring = monitor.variable.equals("true")?true:false;
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
