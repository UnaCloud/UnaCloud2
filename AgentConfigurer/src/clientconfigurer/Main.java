package clientconfigurer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.losandes.utils.ConfigurationReader;
import com.losandes.utils.UnaCloudConstants;

/**
 * Class used to create a local properties file
 * @author Cesar
 *
 */
public class Main {
	
	public static void main(String... args) throws IOException{
		ConfigurationReader propLocal = new ConfigurationReader(UnaCloudConstants.LOCAL_FILE);
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	String vboxPath=propLocal.getStringVariable(UnaCloudConstants.VBOX_PATH);
    	System.out.println("Variable "+UnaCloudConstants.VBOX_PATH+" value: "+ vboxPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	String input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for "+UnaCloudConstants.VBOX_PATH+":");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		propLocal.setStringVariable(UnaCloudConstants.VBOX_PATH, input);
        	}
    	}
    	
    	String vmRunPath= propLocal.getStringVariable(UnaCloudConstants.VMRUN_PATH);
    	System.out.println("Variable "+UnaCloudConstants.VMRUN_PATH+" value: "+ vmRunPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for "+UnaCloudConstants.VMRUN_PATH+":");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		propLocal.setStringVariable(UnaCloudConstants.VMRUN_PATH, input);
        	}
    	}
    	String vmRepoPath=propLocal.getStringVariable(UnaCloudConstants.VM_REPO_PATH);
    	System.out.println("Variable "+UnaCloudConstants.VM_REPO_PATH+" value: "+ vmRepoPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for "+UnaCloudConstants.VM_REPO_PATH+":");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		propLocal.setStringVariable(UnaCloudConstants.VM_REPO_PATH, input);
        	}
    	}
    	String dataPath=propLocal.getStringVariable(UnaCloudConstants.DATA_PATH);
    	System.out.println("Variable "+UnaCloudConstants.DATA_PATH+" value: "+ dataPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for "+UnaCloudConstants.DATA_PATH+":");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		propLocal.setStringVariable(UnaCloudConstants.DATA_PATH, input);
        	}
    	}
    	propLocal.saveConfiguration();
    	br.close();
    }
	
}
