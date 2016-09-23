package unacloud

import grails.transaction.Transactional

import java.util.zip.ZipEntry
import java.nio.file.Files;
import java.util.zip.ZipOutputStream

import com.losandes.utils.UnaCloudConstants;

import unacloud.share.enums.ServerVariableProgramEnum;

/**
 * This service contains all methods to manage configuration: update server variables, manage agent version and download agent files.
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
@Transactional
class ConfigurationService {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

	/**
	 * Sets value in a server variable
	 * @param variable to be modified
	 * @param value to be recorded in variable
	 * @return
	 */
    def setValue(ServerVariable variable, value) {
		variable.putAt("variable", value)
    }
	
	/**
	 * returns the current agent version in system
	 * @return
	 */
	def getAgentVersion(){
		return ServerVariable.findByName(UnaCloudConstants.AGENT_VERSION).variable
	}
	
	/**
	 * Sets the version of agent increasing the minor number of version server variable
	 * @return
	 */
	def setAgentVersion(){
		ServerVariable agentVersion= ServerVariable.findByName(UnaCloudConstants.AGENT_VERSION)
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
		copyFile(zos,UnaCloudConstants.UPDATER_JAR,new File(appDir,"agentSources/"+UnaCloudConstants.UPDATER_JAR),true);
		zos.putNextEntry(new ZipEntry(UnaCloudConstants.GLOBAL_FILE));
		PrintWriter pw=new PrintWriter(zos);
		for(ServerVariable sv:ServerVariable.where{serverOnly == false}.findAll())
			pw.println(sv.name+"="+sv.variable);
		pw.flush();
		zos.closeEntry();
		pw=new PrintWriter(zos);
		zos.putNextEntry(new ZipEntry(UnaCloudConstants.LOCAL_FILE));
		pw.println("DATA_PATH=path_to_save_logs");
		pw.println("VM_REPO_PATH=path_to_local_repository");
		pw.println("VMRUN_PATH=path_to_vmrun.exe");
		pw.println("VBOX_PATH=path_to_VBoxManage.exe");
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
