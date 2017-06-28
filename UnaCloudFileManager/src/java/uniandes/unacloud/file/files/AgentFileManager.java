package uniandes.unacloud.file.files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.common.utils.UnaCloudConstants;

import uniandes.unacloud.file.db.ServerVariableManager;
import uniandes.unacloud.file.db.entities.ServerVariableEntity;

/**
 * Class used to copy files in stream to send files to agent
 * @author CesarF
 *
 */
public class AgentFileManager {

	/**
	 * Prepares the agent files and sends them in a zip.
	 * @param outputStream file output stream for download
	 * @param appDir directory where the zip will be stored
	 * @throws IOException 
	 * @throws SQLException 
	 */
	
	public static void copyAgentOnStream(OutputStream outputStream, Connection con) throws IOException, SQLException {
		ZipOutputStream zos = new ZipOutputStream(outputStream);
		System.out.println(System.getProperty(UnaCloudConstants.ROOT_PATH) + "agentSources/" + UnaCloudConstants.AGENT_JAR);
		copyFile( zos, UnaCloudConstants.AGENT_JAR, new File(System.getProperty(UnaCloudConstants.ROOT_PATH), "agentSources/" + UnaCloudConstants.AGENT_JAR), true);
		zos.putNextEntry(new ZipEntry(UnaCloudConstants.GLOBAL_FILE));
		PrintWriter pw = new PrintWriter(zos);
		List<ServerVariableEntity> variables = ServerVariableManager.getAllVariablesForAgent( con);
		for (ServerVariableEntity sv:variables)
			pw.println(sv.getName() + "=" + sv.getValue());
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
	
	private static void copyFile(ZipOutputStream zos, String filePath, File f, boolean root) throws IOException {
		if (f.isDirectory())
			for (File r : f.listFiles())
				copyFile(zos, r.getName(), r, false);
		else if(f.isFile()) {
			zos.putNextEntry(new ZipEntry(filePath));
			Files.copy(f.toPath(), zos);
			zos.closeEntry();
		}
	}
}
