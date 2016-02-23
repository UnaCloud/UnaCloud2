/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clientupdater;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.losandes.utils.UnaCloudConstants;


/**
 * Main class of UnaCloud Client Updater. This component is responsible for downloading the latest version of unacloud client from unacloud server instance.
 * To do this, this class stores a file containing the latests downloaded version of unacloud client. When starting the physical machine, if this component finds that
 * the current version differs with the version at unacloud server then it downloads the latests version and replace the current version. This component has a little failure probability.
 * @author Clouder
 * 
 */
public class Main {
	   
    public static void main(String... args) throws IOException{
        if(args.length>=1){        	
            startClient(Integer.parseInt(args[0]));
        }
    }

    /**
     * It starts unacloud client with the given operation.  The version is checked and updated only on start operations.
     * @param opcion
     */
    public static void startClient(int opcion) {
        if(opcion==UnaCloudConstants.DELAY){
            opcion=UnaCloudConstants.RUN;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(opcion==UnaCloudConstants.RUN){
        	try {
        		UpdaterAgent updater = new UpdaterAgent();
            	updater.doUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}        	
        }
        try {
        	System.out.println("executing Agent");
            Runtime.getRuntime().exec(new String[]{"java","-jar",UnaCloudConstants.AGENT_JAR,opcion+""});
        } catch (Throwable t) {
            System.out.println("EXE: "+t.getMessage());
        }
    }
   
}