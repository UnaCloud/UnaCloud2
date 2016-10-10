package uniandes.unacloud.agent.platform;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ExecStartParameter;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ExecCreation;

import uniandes.unacloud.agent.execution.entities.Execution;
import uniandes.unacloud.agent.execution.entities.ImageCopy;

/**
 * Implementation of platform abstract class to give support for VirtualBox
 * platform.
 */
public class Docker extends Platform {
	/**
	 * The grace period (the amount of seconds to wait between SIGTERM and
	 * SIGKILL) that is used when stopping a container 
	 */
    private final static int STOP_GRACE_PERIOD_SECONDS = 2;
	
    /**
     * Docker client that will communicate with the daemon. It is built from
     * the DOCKER_HOST environment variable
     */
	private DockerClient client;
	
	/**
	 * Class constructor
	 * @param path Path to this platform executable
	 */
	public Docker(String path) {
		super(path); 
		try {
			client = DefaultDockerClient.fromEnv().build();
		} catch (DockerCertificateException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Sends a stop command to the platform
     * @param image Image copy to be stopped 
     */
    @Override
    public void stopExecution(ImageCopy image){
		try {
			client.stopContainer(image.getExecutionID(), Docker.STOP_GRACE_PERIOD_SECONDS);
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Builds the image and creates a container with it.
     * Sets the execution ID of image
     * @param image Image copy to be registered
     */
    @Override
	public void registerImage(ImageCopy image){
    	// TODO consider adding BuildParams
    	// TODO handle network/ports
    	try {
    		// Attemps to create image from Dockerfile, has no effect if daemon already has the image
			client.build(image.getMainFile().toPath(), new DockerClient.BuildParam[]{});

			ContainerConfig contCfg = ContainerConfig.builder().image(image.getImageName()).build();
			image.setExecutionID(client.createContainer(contCfg).id());
		} catch (DockerException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Removes the container. The container must be stopped before it can be
     * removed.
     * @param image Image copy to be unregistered
     */
    @Override
	public void unregisterImage(ImageCopy image){
        try {
			client.removeContainer(image.getExecutionID());
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Sends a reset message to the platform
     * @param image Image to be restarted
     */
    @Override
    public void restartExecution(ImageCopy image) throws PlatformOperationException {
        try {
			client.restartContainer(image.getExecutionID(), Docker.STOP_GRACE_PERIOD_SECONDS);
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Sends a start message to the platform
     * @param image Image to be started
     */
    @Override
	public void startExecution(ImageCopy image) throws PlatformOperationException {
		try {
			client.startContainer(image.getExecutionID());
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Changes VM configuration
     * @param cores new number of cores for the VM
     * @param ram new RAM value for the VM
     * @param image Copy to be modified
     */
    @Override
    public void configureExecutionHardware(int cores, int ram, ImageCopy image) throws PlatformOperationException {
    	// TODO implement
    	throw new UnsupportedOperationException();
    }
    
    /**
     * Executes a command to the VM itself
     * @param image copy in which command will be executed
     * @param command command to be executed
     * @param args command arguments 
     */
    @Override
    public void executeCommandOnExecution(ImageCopy image,String command, String... args) throws PlatformOperationException {
    	String[] cmd = new String[args.length + 1];
    	cmd[0] = command;
    	for(int i = 1; i < cmd.length; i++) {
    		cmd[i] = args[i];
    	}
    	
    	try {
			ExecCreation exec = client.execCreate(image.getExecutionID(), cmd);
			client.execStart(exec.id(), ExecStartParameter.DETACH);
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    /**
     * Copy a file on the container's filesystem. The path must already exist in the container
     * @param image copy in which file will be pasted
     * @param destinationRoute route in which file will be pasted
     * @param sourceFile file to be copied
     */
    @Override
    public void copyFileOnExecution(ImageCopy image, String destinationRoute, File sourceFile) throws PlatformOperationException {
    	try {
			client.copyToContainer(sourceFile.toPath(), image.getExecutionID(), destinationRoute);
		} catch (DockerException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Takes a snapshot of the container. Unsuported
     * @param image copy of the image that will have the new snapshot
     * @param snapshotname 
     */
    @Override
    public void takeExecutionSnapshot(ImageCopy image,String snapshotname){
        throw new UnsupportedOperationException();
    }
    
    /**
     * Deletes a snapshot of the container. Unsuported
     * @param image copy of the image to delete its snapshot
     * @param snapshotname 
     */
    @Override
    public void deleteExecutionSnapshot(ImageCopy image,String snapshotname){
    	throw new UnsupportedOperationException();
    }
    
    /**
     * Changes the Container's MAC address
     * @param image copy to be modified
     */
    @Override
    public void changeExecutionMac(ImageCopy image) throws PlatformOperationException {
    	// TODO implement
    	throw new UnsupportedOperationException();
    }

    /**
     * Restores a container to its snapshot. Unsuported
     * @param image copy to be reverted
     * @param snapshotname snapshot to which image will be restored
     */
	@Override
	public void restoreExecutionSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Verifies if the container has the specified snapshot. Unsuported
	 * @param image 
	 * @param snapshotname 
	 */
	@Override
	public boolean existsExecutionSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Unregisters all VMs from platform
	 */
	public void unregisterAllVms(){
		try {
			List<Container> containers = client.listContainers(ListContainersParam.allContainers());
			for(Container c : containers) {
				client.removeContainer(c.id());
			}
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clones an image making a new copy
	 * @param source source copy
	 * @param dest empty destination copy
	 */
	@Override
	public void cloneImage(ImageCopy source, ImageCopy dest) {
		// TODO implement
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<Execution> checkExecutions(Collection<Execution> executions) {
		List<Container> containers;
		try {
			// Returns only the running containers
			containers = client.listContainers();
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		
		List<Execution> executionsToDelete = new ArrayList<Execution>();
		
		Outer: for(Execution execution : executions) {
			for(Container c : containers) {
				if(c.id().equals(execution.getId())) {
					continue Outer;
				}
			}
			executionsToDelete.add(execution);
		}
		return executionsToDelete;
	}
}
