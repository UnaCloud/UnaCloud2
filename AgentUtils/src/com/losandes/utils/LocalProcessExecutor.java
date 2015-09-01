package com.losandes.utils;

import java.io.*;

import static com.losandes.utils.Constants.*;

import java.util.Arrays;

/**
 * Responsible for executing commands on local machine
 */
public class LocalProcessExecutor {

    private LocalProcessExecutor() {}

    /**
     * Responsible for executing local commands without output
     * @param inCommand Command to execute
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
     * Responsible for executing local commands with outputs
     * @param inCommand Command to execute
     * @return The output of the command execution
     */
    public static String executeCommandOutput(String...command){
    	System.out.println("Exec: "+Arrays.toString(command));
    	Process p;
        try {
            p = Runtime.getRuntime().exec(command);
        }catch(IOException ex){
            System.out.println("Error: Executable not found");
            return ERROR_MESSAGE+": Executable not found";
        }
        String outputs = "";
        try(BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))){
            for(String line;(line = in.readLine()) != null;)outputs += line + "\n";
        }catch (IOException ex){
            outputs =ERROR_MESSAGE + "executing " + Arrays.toString(command) + " : " + ex.getMessage();
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
    public static void killProcess(String process){
    	executeCommand("taskkill /f /im " + process);    	
    }
    
}
