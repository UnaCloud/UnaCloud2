package clientconfigurer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.losandes.utils.ClientConstants;
import com.losandes.utils.ConfigurationReader;

/**
 * Class used to create a local properties file
 * @author Cesar
 *
 */
public class Main {
	
	public static void main(String... args) throws IOException{
		ConfigurationReader propLocal = new ConfigurationReader(ClientConstants.LOCAL_FILE);
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	String vboxPath=propLocal.getStringVariable(ClientConstants.VBOX_PATH);
    	System.out.println("Variable "+ClientConstants.VBOX_PATH+" value: "+ vboxPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	String input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for "+ClientConstants.VBOX_PATH+":");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		propLocal.setStringVariable(ClientConstants.VBOX_PATH, input);
        	}
    	}
    	
    	String vmRunPath= propLocal.getStringVariable(ClientConstants.VMRUN_PATH);
    	System.out.println("Variable "+ClientConstants.VMRUN_PATH+" value: "+ vmRunPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for "+ClientConstants.VMRUN_PATH+":");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		propLocal.setStringVariable(ClientConstants.VMRUN_PATH, input);
        	}
    	}
    	String vmRepoPath=propLocal.getStringVariable(ClientConstants.VM_REPO_PATH);
    	System.out.println("Variable "+ClientConstants.VM_REPO_PATH+" value: "+ vmRepoPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for "+ClientConstants.VM_REPO_PATH+":");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		propLocal.setStringVariable(ClientConstants.VM_REPO_PATH, input);
        	}
    	}
    	String dataPath=propLocal.getStringVariable(ClientConstants.DATA_PATH);
    	System.out.println("Variable "+ClientConstants.DATA_PATH+" value: "+ dataPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for "+ClientConstants.DATA_PATH+":");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		propLocal.setStringVariable(ClientConstants.DATA_PATH, input);
        	}
    	}
    	propLocal.saveConfiguration();
    	br.close();
    }
	
}
