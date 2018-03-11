package uniandes.unacloud.agent.platform.vmware;

import static uniandes.unacloud.common.utils.UnaCloudConstants.ERROR_MESSAGE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.ImageCopy;
import uniandes.unacloud.agent.host.system.OSFactory;
import uniandes.unacloud.agent.platform.Platform;
import uniandes.unacloud.utils.LocalProcessExecutor;

/**
 * Implementation of platform abstract class to give support for
 * VMware hypervisor.
 */

public abstract class VMwareAbstractHypervisor extends Platform {
	
	public static final String VMW_VMX_CPU = "numvcpus";
    public static final String VMW_VMX_MEMORY = "memsize";
	
	public VMwareAbstractHypervisor(String path) {
		super(path);
	}
	
    @Override
    public void stopExecution(ImageCopy image) {
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T", getType(), "stop", image.getMainFile().getExecutableFile().getPath());
    }

    @Override
    public void restartExecution(ImageCopy image) throws PlatformOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T",getType(), "reset", image.getMainFile().getExecutableFile().getPath());
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }

    @Override
    public void startExecution(ImageCopy image) throws PlatformOperationException {
        correctDataStores();
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T", getType(), "start", image.getMainFile().getExecutableFile().getPath(),"nogui");
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
        sleep(30000);
    }
    
    @Override
    public void executeCommandOnExecution(ImageCopy image,String command, String... args) throws PlatformOperationException {
        List<String> com = new ArrayList<>();
        Collections.addAll(com, getExecutablePath(), "-T", getType(), "-gu", image.getImage().getUsername(), "-gp", image.getImage().getPassword(), "runProgramInGuest", image.getMainFile().getExecutableFile().getPath());
        com.add(command);
        Collections.addAll(com,args);
        String h = LocalProcessExecutor.executeCommandOutput(com.toArray(new String[0]));
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }

    @Override
    public void copyFileOnExecution(ImageCopy image, String destinationRoute, File sourceFile) throws PlatformOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T", getType(), "-gu", image.getImage().getUsername(), "-gp",image.getImage().getPassword(), "copyFileFromHostToGuest", image.getMainFile().getExecutableFile().getPath(), sourceFile.getAbsolutePath(), destinationRoute);
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }

    @Override
    public void takeExecutionSnapshot(ImageCopy image,String snapshotname) throws PlatformOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T" ,getType(), "snapshot", image.getMainFile().getExecutableFile().getPath(), snapshotname);
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }
    
    @Override
    public void deleteExecutionSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
    	String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T", getType(), "deleteSnapshot", image.getMainFile().getExecutableFile().getPath(), snapshotname);
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    };
    
    @Override
    public void configureExecutionHardware(int cores, int ram, ImageCopy image) throws PlatformOperationException {
    	if (cores != 0 && ram != 0 ) {
            new VMXContext(image.getMainFile().getExecutableFile().getPath()).changeVMXFileContext(cores,ram);
        }
    }
    
    @Override
    public boolean existsExecutionSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
    	String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T", getType(), "listSnapshots", image.getMainFile().getExecutableFile().getPath());
    	return h!=null&&h.contains(snapshotname);
    }
    
    @Override
    public void restoreExecutionSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
    	String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T", getType(), "revertToSnapshot", image.getMainFile().getExecutableFile().getPath(), snapshotname);
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }
    
    /**
     * Corrects datastores file using datastores file in program data
     */
    private void correctDataStores() {
        try {
            FileInputStream fis = new FileInputStream("./datastores.xml");
            FileOutputStream fos = new FileOutputStream(OSFactory.getOS().getProgramDataPath());
            byte[] b = new byte[1024];
            for (int n; (n = fis.read(b)) != -1;) {
                fos.write(b, 0, n);
            }
            fis.close();
            fos.close();
        } catch (Throwable th) {
        }
    }

    public void changeExecutionMac(ImageCopy image) throws PlatformOperationException {
    	
    }
    
	@Override
	public void registerImage(ImageCopy image) {
	}
	
	@Override
	public void unregisterImage(ImageCopy image) {
	}
	/**
	 * Returns type VMware execution
	 * @return type of VMware platform
	 */
	protected abstract String getType();
	
	@Override
	public void cloneImage(ImageCopy source, ImageCopy dest) {
		LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "clone", source.getMainFile().getExecutableFile().getAbsolutePath(), dest.getMainFile().getExecutableFile().getAbsolutePath(), "full", "unacloudbase");
		sleep(20000);
		try {
			takeExecutionSnapshot(dest,"unacloudbase");
		} catch (PlatformOperationException e) {
			e.printStackTrace();
		}
        unregisterImage(dest);
	}	
	
	@Override
	public List<Execution> checkExecutions(Collection<Execution> executions) {
		List<Execution> executionsToDelete = new ArrayList<Execution>();
		List<String> list = new ArrayList<String>();
		try {
			String[] result = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "-T", getType(), "list").split("\n|\r");
			for (String vm: result)if(!vm.startsWith("Total running VMs")) {
				String deploy = vm.split(" ")[1];
				list.add(deploy.substring(deploy.indexOf("/"), deploy.lastIndexOf(".")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		for (Execution execution: executions) {
			boolean isRunning = false;
			for (String exeInHypervisor: list) {
				if (exeInHypervisor.contains(execution.getImage().getImageName())) {
					isRunning = true;
					break;
				}
			}	
			if(!isRunning)executionsToDelete.add(execution);	
		}
		return executionsToDelete;
	}
}