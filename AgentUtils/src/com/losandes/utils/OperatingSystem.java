package com.losandes.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.losandes.utils.LocalProcessExecutor;
import static com.losandes.utils.Constants.*;

/**
 * @author Eduardo Rosales 
 * @author CesarF
 * Responsible for executing operating system operations
 */
public class OperatingSystem {

    private String operatingSystemName;
    private String operatingSystemCurrentUser;
	private String operatingArchitecture = System.getProperty("os.arch").toLowerCase();

    public OperatingSystem() {
        operatingSystemName = getOperatingSystemName();
        operatingSystemCurrentUser = getUserName();
        operatingArchitecture = getOperatingSystemArchitect();
    }
    /**
     * Responsible for obtaining the operating system name
     * @return
     */
    public String getOperatingSystemName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * Responsible for obtaining the operating system version
     * @return
     */
    public String getOperatingSystemVersion() {
        return System.getProperty("os.version");
    }

    /**
     * Responsible for obtaining the operating system architect
     * @return
     */
    public String getOperatingSystemArchitect() {
        return System.getProperty("os.arch").toLowerCase();
    }

    /**
     * Responsible for turning off the local operating system
     * @return
     */
    public String turnOff() {
        String result = "";
        if (operatingSystemName.contains("windows")) {
            LocalProcessExecutor.executeCommand(WINDOWS_TURN_OFF_COMMAND);
        } else if (operatingSystemName.contains("mac")) {
            LocalProcessExecutor.executeCommand("sh " + BIN + PATH_SEPARATOR + MAC_TURN_OFF_FILE_COMMAND);
        } else if (operatingSystemName.contains("linux")) {
            LocalProcessExecutor.executeCommand("sh " + BIN + PATH_SEPARATOR + LINUX_TURN_OFF_FILE_COMMAND);
        } else {
            result = ERROR_MESSAGE + "The operating system is not supported: " + operatingSystemName;
            System.err.println(result);
        }
        return result;
    }

    /**
     * Responsible for restarting the local operating system
     * @return
     */
    public String restart() {
        String result = "";
        if (operatingSystemName.contains("windows")) {
            LocalProcessExecutor.executeCommand(WINDOWS_RESTART_COMMAND);
        } else if (operatingSystemName.contains("mac")) {
            LocalProcessExecutor.executeCommand("sh " + BIN + PATH_SEPARATOR + MAC_RESTART_FILE_COMMAND);
        } else if (operatingSystemName.contains("linux")) {
            LocalProcessExecutor.executeCommand("sh " + BIN + PATH_SEPARATOR + LINUX_RESTART_FILE_COMMAND);
        } else {
            result = ERROR_MESSAGE + "The operating system is not supported: " + operatingSystemName;
            System.err.println(result);
        }
        return result;
    }

    /**
     * Responsible for logout the local operating system
     * @return
     */
    public String logOut() {
        String result = "";
        if (operatingSystemName.contains("windows")) {
            LocalProcessExecutor.executeCommand(WINDOWS_LOGOUT_COMMAND);
        } else if (operatingSystemName.contains("linux")) {
            LocalProcessExecutor.executeCommand("sh " + BIN + PATH_SEPARATOR + LINUX_LOGOUT_FILE_COMMAND + operatingSystemCurrentUser);
        } else {
            result = ERROR_MESSAGE + "The operating system is not supported: " + operatingSystemName;
            System.err.println(result);
        }
        return result;
    }
    public static String getUserName() {
        String userName = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe","/c","quser"});
            InputStream is = p.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                br.readLine();
                for (String linea,lw; (linea = br.readLine()) != null;) {
                	lw=linea.toLowerCase();
                	if(lw.contains("active")||lw.contains("activo")){
                		final String user = linea.trim().split(" |\t")[0].replaceAll("[^\\w\\.-]","");
                        userName = (userName == null ? "" : (userName+";")) + user;
                	}
                    
                }
            }
            p.destroy();
        } catch (IOException ex) {
        }
        return userName;
    }
    public static String getWhoAmI() {
        String userName = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"whoami"});
            InputStream is = p.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                userName = br.readLine();
            }
            p.destroy();
        } catch (IOException ex) {
        }
        return userName;
    }
    /**
     * Operations to get constants in OS
     */

    public boolean isWindows() {		 
		return (operatingSystemName.indexOf("win") >= 0); 
	} 
	public boolean isMac() { 
		return (operatingSystemName.indexOf("mac") >= 0); 
	} 
	public boolean isUnix() { 
		return (operatingSystemName.indexOf("nix") >= 0 || operatingSystemName.indexOf("nux") >= 0 ); 
	} 
	public boolean isAix() { 
		return (operatingSystemName.indexOf("aix") > 0 ); 
	} 
	public boolean isSolaris() { 
		return (operatingSystemName.indexOf("sunos") >= 0); 
	}	
	public boolean isFreeBSD(){
		return (operatingSystemName.indexOf("free") >=0);
	}
	public boolean isHpUx(){
		return (operatingSystemName.indexOf("hp") >=0);
	}
	public boolean isAMD64(){
		return (operatingArchitecture.indexOf("amd64") >= 0);
	}
	public boolean isX86(){
		return (operatingArchitecture.indexOf("x86") >= 0);
	}
	public boolean isSparc(){
		return (operatingArchitecture.indexOf("sparc") >= 0);
	}
	public boolean isIA(){
		return (operatingArchitecture.indexOf("ia64") >=0);
	}
	public boolean isPaRisc(){
		return (operatingArchitecture.indexOf("risc") >= 0);
	}
	public boolean isPpc(){
		return (operatingArchitecture.indexOf("ppc") >= 0);
	}
	public boolean isPpc64(){
		return (operatingArchitecture.indexOf("ppc64") >= 0);
	}
	public boolean isSparc64(){
		return (operatingArchitecture.indexOf("sparc64") >= 0);
	}
}

