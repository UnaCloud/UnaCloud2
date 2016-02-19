package clientupdater;

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

import com.losandes.utils.ClientConstants;
import com.losandes.utils.ConfigurationReader;

/**
 * This class verifies version and updates all agent files.
 * @author Cesar
 *
 */
public class UpdaterAgent {

    final static File versions = new File(ClientConstants.VERSION_FILE);    

    static ConfigurationReader propReader;   
    
    public UpdaterAgent() throws IOException {
    	propReader = new ConfigurationReader(ClientConstants.GLOBAL_FILE);
	}
	
    /**
     * Verifies with server agent version and downloads last agent files.
     * @throws Exception
     */
	public void doUpdate() throws Exception{
		final List<String> versionsFile = gerVersionFile();
		final int port=propReader.getIntegerVariable(ClientConstants.FILE_CLIENT_VERSION_PORT);
		final String ip=propReader.getStringVariable(ClientConstants.FILE_SERVER_IP);

		try(Socket s=new Socket(ip,port);DataOutputStream ds=new DataOutputStream(s.getOutputStream());DataInputStream is = new DataInputStream(s.getInputStream())){	
			String versionServer = is.readUTF(); 
	    	System.out.println("Version is "+versionsFile.get(0)+" - server is "+versionServer);
	    	if(!versionServer.equals(versionsFile.get(0))){
	    		try {
		    		for (int e = 1; e < versionsFile.size(); e++) {
		                File c = new File(versionsFile.get(e));
		    			if (c.exists()&&!c.getName().equals("vars")&&!c.getName().equals(ClientConstants.UPDATER_JAR)){
		                	c.delete();
		                }
		            }
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		try(PrintWriter versionFile = new PrintWriter(new FileOutputStream(versions),false)){
	    			versionFile.println(versionServer);	    			
	    			try(ZipInputStream zis = new ZipInputStream(is);){
	    				byte[] buffer=new byte[100*1024];
	    				for(ZipEntry ze;(ze=zis.getNextEntry())!=null;){
	    					versionFile.println(ze.getName());
	    					File output=new File(ze.getName());
	    					if(output.getParentFile()!=null)output.getParentFile().mkdirs();
	    					try(FileOutputStream fos=new FileOutputStream(output)){
	    						for(int n;(n=zis.read(buffer))!=-1;)fos.write(buffer,0,n);
	    					}
	    				}
	    			}  	
	        		versionFile.flush();
	                versionFile.close();
	    		} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	    	}	
	    	else{
	    		ds.writeUTF("Thanks");
	    	}
			s.close();
		}catch(Exception e){
			e.printStackTrace();			
		}
	}

	 /**
     * Returns the information contained on stored version file.
     * @return
     */
    private ArrayList<String> gerVersionFile() {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            BufferedReader ver = new BufferedReader(new FileReader(versions));
            for (String h; (h = ver.readLine()) != null;)ret.add(h);
            ver.close();
            if (ret.size() == 0)ret.add("NOVERSION");
        } catch (IOException ex) {
            ret = new ArrayList<String>();
            ret.add("NOVERSION");
        }
        return ret;
    }
}
