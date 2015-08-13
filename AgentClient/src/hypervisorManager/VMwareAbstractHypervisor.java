package hypervisorManager;

import static com.losandes.utils.Constants.ERROR_MESSAGE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.losandes.utils.LocalProcessExecutor;

/**
 * Implementation of hypervisor abstract class to give support for
 * VMwareWorkstation hypervisor.
 */

public abstract class VMwareAbstractHypervisor extends Hypervisor{
	public VMwareAbstractHypervisor(String path) {
		super(path);
	}
    @Override
    public void stopVirtualMachine(ImageCopy image){
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"-T",getType(),"stop",image.getMainFile().getPath());
    }

    @Override
    public void restartVirtualMachine(ImageCopy image) throws HypervisorOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"-T",getType(),"reset",image.getMainFile().getPath());
        if (h.contains(ERROR_MESSAGE)) {
            throw new HypervisorOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }

    @Override
    public void startVirtualMachine(ImageCopy image) throws HypervisorOperationException {
        correctDataStores();
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"-T",getType(),"start",image.getMainFile().getPath(),"nogui");
        if (h.contains(ERROR_MESSAGE)) {
            throw new HypervisorOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
        sleep(30000);
    }
    
    @Override
    public void executeCommandOnMachine(ImageCopy image,String command, String... args) throws HypervisorOperationException {
        List<String> com=new ArrayList<>();
        Collections.addAll(com, getExecutablePath(),"-T",getType(),"-gu",image.getImage().getUsername(),"-gp",image.getImage().getPassword(),"runProgramInGuest",image.getMainFile().getPath());
        com.add(command);
        Collections.addAll(com,args);
        String h = LocalProcessExecutor.executeCommandOutput(com.toArray(new String[0]));
        if (h.contains(ERROR_MESSAGE)) {
            throw new HypervisorOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }

    @Override
    public void copyFileOnVirtualMachine(ImageCopy image, String destinationRoute, File sourceFile) throws HypervisorOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"-T",getType(),"-gu",image.getImage().getUsername(),"-gp",image.getImage().getPassword(),"copyFileFromHostToGuest",image.getMainFile().getPath(),sourceFile.getAbsolutePath(),destinationRoute);
        if (h.contains(ERROR_MESSAGE)) {
            throw new HypervisorOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }

    @Override
    public void takeVirtualMachineSnapshot(ImageCopy image,String snapshotname) throws HypervisorOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"-T",getType(),"snapshot",image.getMainFile().getPath(),snapshotname);
        if (h.contains(ERROR_MESSAGE)) {
            throw new HypervisorOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }
    @Override
    public void deleteVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
    	String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"-T",getType(),"deleteSnapshot",image.getMainFile().getPath(),snapshotname);
        if (h.contains(ERROR_MESSAGE)) {
            throw new HypervisorOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    };
    @Override
    public void configureVirtualMachineHardware(int cores, int ram, ImageCopy image) throws HypervisorOperationException {
    	if(cores!=0&&ram!=0){
            new Context(image.getMainFile().getPath()).changeVMXFileContext(cores,ram);
       }
    }
    @Override
    public boolean existsVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
    	String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"-T",getType(),"listSnapshots",image.getMainFile().getPath());
    	return h!=null&&h.contains(snapshotname);
    }
    @Override
    public void restoreVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
    	String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"-T",getType(),"revertToSnapshot",image.getMainFile().getPath(),snapshotname);
        if (h.contains(ERROR_MESSAGE)) {
            throw new HypervisorOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
    }
    private void correctDataStores() {
        try {
            FileInputStream fis = new FileInputStream("./datastores.xml");
            FileOutputStream fos = new FileOutputStream("C:\\ProgramData\\VMware\\hostd\\datastores.xml");
            byte[] b = new byte[1024];
            for (int n; (n = fis.read(b)) != -1;) {
                fos.write(b, 0, n);
            }
            fis.close();
            fos.close();
        } catch (Throwable th) {
        }
    }

    public void changeVirtualMachineMac(ImageCopy image) throws HypervisorOperationException {
    }
	@Override
	public void registerVirtualMachine(ImageCopy image) {
	}
	@Override
	public void unregisterVirtualMachine(ImageCopy image) {
	}
	public abstract String getType();
	@Override
	public void cloneVirtualMachine(ImageCopy source, ImageCopy dest) {
		LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"clone",source.getMainFile().getAbsolutePath(),dest.getMainFile().getAbsolutePath(),"full","unacloudbase");
		sleep(20000);
		try {
			takeVirtualMachineSnapshot(dest,"unacloudbase");
		} catch (HypervisorOperationException e) {
			e.printStackTrace();
		}
        unregisterVirtualMachine(dest);
	}
}