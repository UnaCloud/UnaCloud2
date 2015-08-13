package clientconfigurer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.losandes.utils.VariableManager;

public class Main {
	
	public static void main(String... args) throws IOException{
    	VariableManager local= VariableManager.local;
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
    	String hostname=local.getStringValue("LOCAL_HOSTNAME");
    	System.out.println("Variable LOCAL_HOSTNAME value: "+ hostname);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	String input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for LOCAL_HOSTNAME:");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		local.setStringValue("LOCAL_HOSTNAME", input);
        	}
    	}
    	String vboxPath=local.getStringValue("VBOX_PATH");
    	System.out.println("Variable VBOX_PATH value: "+ vboxPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for VBOX_PATH:");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		local.setStringValue("VBOX_PATH", input);
        	}
    	}
    	
    	String vmRunPath= local.getStringValue("VMRUN_PATH");
    	System.out.println("Variable VMRUN_PATH value: "+ vmRunPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for VMRUN_PATH:");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		local.setStringValue("VMRUN_PATH", input);
        	}
    	}
    	String vmRepoPath=local.getStringValue("VM_REPO_PATH");
    	System.out.println("Variable VM_REPO_PATH value: "+ vmRepoPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for VM_REPO_PATH:");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		local.setStringValue("VM_REPO_PATH", input);
        	}
    	}
    	String dataPath=local.getStringValue("DATA_PATH");
    	System.out.println("Variable DATA_PATH value: "+ dataPath);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for DATA_PATH:");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		local.setStringValue("DATA_PATH", input);
        	}
    	}
    	String path_power=local.getStringValue("PATH_POWERLOG");
    	System.out.println("Variable PATH_POWERLOG: "+ path_power);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for PATH_POWERLOG:");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		local.setStringValue("PATH_POWERLOG", input);
        	}
    	}
    	String cpu_Path=local.getStringValue("LOG_CPU_PATH");
    	System.out.println("Variable LOG_CPU_PATH: "+ cpu_Path);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for LOG_CPU_PATH:");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		local.setStringValue("LOG_CPU_PATH", input);
        	}
    	}
    	String energy_Path=local.getStringValue("LOG_ENERGY_PATH");
    	System.out.println("Variable LOG_ENERGY_PATH: "+ energy_Path);
    	System.out.println("Do you wanna change this value? (Y/N)");
    	input = br.readLine();
    	if (input.equalsIgnoreCase("y")){
    		System.out.println("Enter new value for LOG_ENERGY_PATH:");
        	input = br.readLine();
        	if(!(input.isEmpty())){
        		local.setStringValue("LOG_ENERGY_PATH", input);
        	}
    	}
    	br.close();
    }
	
}
