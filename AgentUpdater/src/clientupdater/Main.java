/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clientupdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class of UnaCloud Client Updater. This component is responsible for downloading the latests version of unacloud client from unacloud server instance.
 * To do this, this class stores a file containing the latests downloaded version of unacloud client. When starting the physical machine, if this component finds that
 * the current version differs with the version at unacloud server then it downloads the latests version and replace the current version. This component has a little failure probability.
 * @author Clouder
 */
public class Main {
    final static File root = new File("./");
    final static File versions = new File("versions.txt");
    static String SERVER_IP = "";
    public static void main(String... args) throws FileNotFoundException{
    	VariableManager.init("./vars");
        SERVER_IP = VariableManager.getStringValue("CLOUDER_SERVER_IP");
        if(args.length>=1){
            startClient(Integer.parseInt(args[0]));
        }
    }

    /**
     * It starts unacloud client with the given operation. The operations are: start, stop, login and logoff. The version is checked and updated only on start operations.
     * @param opcion
     */
    public static void startClient(int opcion) {
        if(opcion==6){
            opcion=1;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(opcion==1)doUpdate();
        try {
        	System.out.println("executing Agent");
            Runtime.getRuntime().exec(new String[]{"java","-jar","ClouderClient.jar",opcion+""});
        } catch (Throwable t) {
            System.out.println("EXE: "+t.getMessage());
        }
    }

    /**
     * This method checks the current version and compares it with the version stored on unacloud server. If the version doesn't match then the newest version is downloaded and installed.
     */
    private static void doUpdate() {
    	final List<String> versionsFile = gerVersionFile();
    	String version=AbstractGrailsCommunicator.getVersion();
    	System.out.println("Version es "+version+" cuando server es "+versionsFile.get(0));
    	if(!versionsFile.get(0).equals(version)){
    		//for(File c:new File(".").listFiles()){
    		for (int e = 1; e < versionsFile.size(); e++) {
                File c = new File(versionsFile.get(e));
    			/*if(c.isDirectory()){
    				for(File r:c.listFiles())r.delete();
    				c.delete();
    			}
    			else */if (c.exists()&&!c.getName().equals("vars")&&!c.getName().equals("ClientUpdater.jar")){
                	c.delete();
                }
            }
    		try(PrintWriter versionFile = new PrintWriter(new FileOutputStream(versions),false)){
    			versionFile.println(version);
        		AbstractGrailsCommunicator.getAgentZip(versionFile);
        		versionFile.flush();
                versionFile.close();
    		} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    }
    /**
     * Returns the information contained on stored version file.
     * @return
     */
    private static ArrayList<String> gerVersionFile() {
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