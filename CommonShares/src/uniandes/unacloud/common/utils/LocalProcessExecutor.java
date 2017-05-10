package uniandes.unacloud.common.utils;

import java.io.*;


import java.util.Arrays;

/**
 * Responsible for executing commands on local machine
 */
public class LocalProcessExecutor {

    private LocalProcessExecutor() {}

    /**
     * Responsible for executing local commands without output
     * @param command Command to execute
     * @return If the command was successfully execute or nor
     */
    public static boolean executeCommand(String command){
    	System.out.println("Exec: "+command);
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
    public static boolean executeCommand(String[] cmdarray){
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
    public static String executeCommandOutput(String...command){
    	System.out.println("Exec: "+Arrays.toString(command));
    	Process p;
        try {
            p = Runtime.getRuntime().exec(command);
        }catch(IOException ex){
            System.out.println("Error: Executable not found");
            return "Error: Executable not found";
        }
        String outputs = "";
        try(BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))){
            for(String line;(line = in.readLine()) != null;)outputs += line + "\n";
        }catch (IOException ex){
            outputs = "Error: executing " + Arrays.toString(command) + " : " + ex.getMessage();
        }
        try(BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()))){
            for(String line;(line = in.readLine()) != null;)outputs += line + "\n";
        }catch (IOException ex){
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
    @SuppressWarnings("unused")
	public static String createProcess(String process){
    	System.out.println("Exec process: ");
    	ProcessBuilder pb = new ProcessBuilder(process);
    	Process pr = null;
    	try {
			pr = pb.start();
		} catch (IOException e) {
			if(pr!=null)return pr.getErrorStream().toString();
			return e.getMessage();
		}
    	return pr.getInputStream().toString();
    }
    /**
     * Validates if process is running
     * Only valid for Windows operating system
     * @param process
     * @return true if is running, false in case not
     */
    public static boolean processIsRunning(String process) {
		String line;
		Process p;
		try {
			p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			//Elimina el header del stream
			br.readLine(); br.readLine(); br.readLine();
			
			while((line = br.readLine()) != null) {
				String[] tmp = line.split(" ");
				if(tmp[0].equals(process)) return true;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
    /**
     * Only valid for Windows operating system
     * @param process
     */
    public static void killProcess(String process){
    	executeCommand("taskkill /f /im " + process);    	
    }
    
}
