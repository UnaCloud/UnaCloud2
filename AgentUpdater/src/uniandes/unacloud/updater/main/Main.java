/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uniandes.unacloud.updater.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import uniandes.unacloud.common.utils.UnaCloudConstants;


/**
 * Main class of UnaCloud Client Updater. This component is responsible for downloading the latest version of UnaCloud client from UnaCloud server instance.
 * To do this, this class stores a file containing the latest version of UnaCloud client. When starting the physical machine, if this component finds that
 * the current version differs with the version at UnaCloud server then it downloads the latest version and replace the current version.
 * @author Clouder
 * @author CesarF
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
				System.exit(0);
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