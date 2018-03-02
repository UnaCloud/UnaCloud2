package uniandes.unacloud.updater;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import uniandes.unacloud.common.utils.ConfigurationReader;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * This class verifies version and updates all agent files.
 * @author CesarF
 * @author Clouder
 *
 */
public class UpdaterAgent {

	/**
	 * Representation of version file
	 */
    private final static File versions = new File(UnaCloudConstants.VERSION_FILE);    

    /**
     * Representation of properties file reader
     */
    private static ConfigurationReader propReader;   
    
    /**
     * Creates a new Agent updater
     * @throws IOException
     */
    public UpdaterAgent() throws IOException {
    	propReader = new ConfigurationReader(UnaCloudConstants.GLOBAL_FILE);
	}
	
    /**
     * Verifies with server agent version and downloads last agent files.
     * @throws Exception
     */
	public void doUpdate() throws Exception {
		final List<String> versionsFile = getVersionFile();
		final int port = propReader.getIntegerVariable(UnaCloudConstants.VERSION_MANAGER_PORT);
		final String ip = propReader.getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
		System.out.println("Connecting to server " + ip + ":" + port);
		try (Socket s = new Socket(ip, port); 
				DataOutputStream ds = new DataOutputStream(s.getOutputStream());
				DataInputStream is = new DataInputStream(s.getInputStream())) {	
			ds.writeInt(UnaCloudConstants.REQUEST_AGENT_VERSION);
			String versionServer = is.readUTF(); 
	    	System.out.println("Version is " + versionsFile.get(0) + " - server is " + versionServer);
	    	if (!versionServer.equals(versionsFile.get(0))) {
	    		try {
		    		for (int e = 1; e < versionsFile.size(); e++) {
		                File c = new File(versionsFile.get(e));
		    			if (c.exists() && !c.getName().equals(UnaCloudConstants.GLOBAL_FILE) && !c.getName().equals(UnaCloudConstants.UPDATER_JAR))
		                	c.delete();
		            }
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		System.out.println("Requesting for agent client");
	    		ds.writeInt(UnaCloudConstants.GIVE_ME_FILES);
	    		try (PrintWriter versionFile = new PrintWriter(new FileOutputStream(versions), false); 
	    				ZipInputStream zis = new ZipInputStream(is);) {	    			
    				byte[] buffer = new byte[100 * 1024];
    				for (ZipEntry ze; (ze = zis.getNextEntry()) != null;) {
    					System.out.println(ze.getName());
    					versionFile.println(ze.getName());
    					File output = new File(ze.getName());
    					if (output.getParentFile() != null)
    						output.getParentFile().mkdirs();
    					FileOutputStream fos = new FileOutputStream(output);
    					for(int n; (n = zis.read(buffer)) != -1;)
    						fos.write(buffer, 0, n);
    					fos.close();
    				}
    				versionFile.println(versionServer);
	        		versionFile.flush();
	                versionFile.close();
	    		} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	    	}	
	    	else 
	    		ds.writeInt(0);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}

	 /**
     * Returns the information contained on stored version file.
     * @return list or files names
     */
    private ArrayList<String> getVersionFile() {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            BufferedReader ver = new BufferedReader(new FileReader(versions));
            for (String h; (h = ver.readLine()) != null;)
            	ret.add(h);
            ver.close();
            if (ret.size() == 0)
            	ret.add("NOVERSION");
        } catch (IOException ex) {
            ret = new ArrayList<String>();
            ret.add("NOVERSION");
        }
        return ret;
    }
}
