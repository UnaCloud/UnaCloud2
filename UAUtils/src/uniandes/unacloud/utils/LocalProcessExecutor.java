package uniandes.unacloud.utils;

import java.io.*;


import java.util.Arrays;

/**
 * Responsible for executing commands on local machine
 */
public class LocalProcessExecutor {

	/**
	 * Creates a new LocalProcessExecutor
	 */
    private LocalProcessExecutor() {
    	
    }

    /**
     * Responsible for executing local commands without output
     * @param command Command to execute
     * @return If the command was successfully execute or nor
     */
    public static boolean executeCommand(String command) {
    	System.out.println("Exec: " + command);
        try {
            Runtime.getRuntime().exec(command).waitFor();
        } catch (Exception ex) {
        	ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Responsible for executing local commands without output
     * @param cmdarray String array where the zeroth position is the command and the next entries are the command parameters
     * @return If the command was successfully execute or nor
     */
    public static boolean executeCommand(String[] cmdarray) {
    	String tempMsg = cmdarray[0];
    	
    	for (int i = 1; i < cmdarray.length; i++) {
			tempMsg += " " + cmdarray[i];
		}
    	
    	System.out.println("Exec: "+tempMsg);
        try {
            Runtime.getRuntime().exec(cmdarray).waitFor();
        } catch (Exception ex) {
        	ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Responsible for executing local commands with outputs
     * @param command Command to execute
     * @return The output of the command execution
     */
    public static String executeCommandOutput(String...command) {
    	System.out.println("Exec: " + Arrays.toString(command));
    	Process p;
        try {
            p = Runtime.getRuntime().exec(command);
        } catch(IOException ex) {
            System.out.println("Error: Executable not found");
            return "Error: Executable not found";
        }
        String outputs = "";
        try(BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            for (String line; (line = in.readLine()) != null;)
            	outputs += line + "\n";
        } catch (IOException ex) {
            outputs = "Error: executing " + Arrays.toString(command) + " : " + ex.getMessage();
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            for (String line; (line = in.readLine()) != null;)
            	outputs += line + "\n";
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println(outputs);
        return outputs;
    }
    
    /**
     * Responsible to create a new process 
     * @param process to be created
     * @return
     */
	public static String createProcess(String process) {
    	System.out.println("Exec process: " + process);
    	ProcessBuilder pb = new ProcessBuilder(process);
    	Process pr = null;
    	try {
    		pr = pb.start();
		} catch (Exception e) {
			if (pr != null) return pr.getErrorStream().toString();
			return e.getMessage();
		}
    	return pr.getInputStream().toString();
    }  
   
    
}
