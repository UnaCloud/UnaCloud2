package hypervisorManager;

import static com.losandes.utils.Constants.DOUBLE_QUOTE;
import static com.losandes.utils.Constants.ERROR_MESSAGE;
import static com.losandes.utils.Constants.OK_MESSAGE;
import static com.losandes.utils.Constants.VMW_VMX_CPU;
import static com.losandes.utils.Constants.VMW_VMX_MEMORY;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Responsible for changing the .vmx configuration file for executing a virtual machine fulfilling the user context
 */
public class Context {

    public File vmxFile;

    /**
     * Constructor method
     * @param vmxPath
     */
    public Context(String vmxPath) {
        if (vmxPath != null) {
            vmxFile = new File(vmxPath.replace("\"",""));
        }
    }

    /**
     * Responsible for sorting the .vmx file context process
     * @param vmCores
     * @param vmMemory
     * @return
     */
    public String changeVMXFileContext(int vmCores, int vmMemory) {
        String[][] vmxParameters = {/*{VMW_VMX_HW, VMW_VMX_HW_VER},*/ {VMW_VMX_CPU, ""+vmCores}, {VMW_VMX_MEMORY, ""+vmMemory},{"snapshot.action","keep"},{"priority.ungrabbed","idle"}};
        return changeVMXparameter(vmxParameters);
    }

    /**
     * Responsible for converting a .vmx file to a String
     * @param vmxPath
     * @return
     */
    private static List<String> convertVMXToString(String vmxPath) {
    	System.out.println("convertVMXToString "+vmxPath);
        try {
			return Files.readAllLines(Paths.get(vmxPath),Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

    /**
     * Prints a new VMX file using a collection of string properties
     * @param vmxPath The path of the output VMX file
     * @param lines The content of the file
     */
    private static void convertStrignToVMX(String vmxPath,List<String> lines) {
        try(PrintWriter pw = new PrintWriter(vmxPath)){
            for(int e=0;e<lines.size();e++)pw.println(lines.get(e));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Responsible for changing a vmx parameter value or create it if not exist (only compatible with VMWare Workstation 6.5)
     * @param vmxParameters [][] = {{VMW_VMX_HW, VMW_VMX_HW_VER}, {VMW_VMX_CPU, vmCores}, {VMW_VMX_MEMORY, vmMemory}}
     */
    private String changeVMXparameter(String[][] vmxParameters) {
        List<String> splittedString = convertVMXToString(vmxFile.getPath());
        if(splittedString==null)return ERROR_MESSAGE;
        boolean found = false;
        for (int r = 0; r < vmxParameters.length; r++) {
            found = false;
            for (int i = 0; i < splittedString.size()&&!found; i++) {
                if (splittedString.get(i).contains(vmxParameters[r][0])) {
                    splittedString.set(i,vmxParameters[r][0] + " = " + DOUBLE_QUOTE + vmxParameters[r][1] + DOUBLE_QUOTE + " ");
                    found = true;
                }
            }
            if(!found){
                splittedString.add(vmxParameters[r][0] + " = " + DOUBLE_QUOTE + vmxParameters[r][1] + DOUBLE_QUOTE + " ");
            }
        }
        convertStrignToVMX(vmxFile.getPath(), splittedString);
        return OK_MESSAGE;
    }
}