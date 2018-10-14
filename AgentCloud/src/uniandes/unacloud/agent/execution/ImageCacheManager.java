package uniandes.unacloud.agent.execution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.Image;
import uniandes.unacloud.agent.execution.domain.ImageCopy;
import uniandes.unacloud.agent.execution.domain.ImageStatus;
import uniandes.unacloud.agent.host.system.OperatingSystem;
import uniandes.unacloud.agent.net.download.DownloadImageTask;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.net.torrent.TorrentClient;
import uniandes.unacloud.agent.utils.SystemUtils;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.utils.file.FileProcessor;
import uniandes.unacloud.utils.security.HashGenerator;
import static uniandes.unacloud.common.utils.UnaCloudConstants.*;

/**
 * Responsible to manage list of images in cache
 * @author Clouder
 */
public class ImageCacheManager {

    /**
     * Path of current image repository
     */
    private static final String machineRepository = VariableManager.getInstance().getLocal().getStringVariable(VM_REPO_PATH);

    /**
     * Represents file where image list is stored
     */
    private static final File imageListFile = new File("imageList");

    /**
     * Represents list of images currently stored in repository
     */
    private static Map<Long, Image> imageList = null;

    /**
     * Returns a free copy of the image
     *
     * @param execution Given execution
     * @return type Type of transmission protocol
     * @throws Exception
     */
    public static ImageCopy getFreeImageCopy(Execution execution, TransmissionProtocolEnum type) throws Exception {
        System.out.println("\tgetFreeImageCopy " + execution.getImageId());
        Image vmi = getImage(execution.getImageId());
        System.out.println("\tImage for searching " + vmi.getId());
        ImageCopy source;
        ImageCopy dest;
        synchronized (vmi) {
            for (ImageCopy copy : vmi.getImageCopies()) {
                System.out.println("\t"+copy.getImageName()+" "+copy.getMainFile().getFilePath());
            }
            System.out.println("\thas " + vmi.getImageCopies().size() + " copies");
            if (vmi.getImageCopies().isEmpty()) {
                ImageCopy copy = new ImageCopy();
                try {
                    DownloadImageTask.downloadImageCopy(vmi, copy, machineRepository, type);
                    saveImages();
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                    throw ex;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new ExecutionException("Error downloading image " + ex.getMessage(), ex);
                }
                System.out.println("\t\t downloaded");
                ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.SUCCESS, "Start configuring");
                return copy;
            } else {
                for (ImageCopy copy : vmi.getImageCopies()) {
                    if (copy.getStatus() == ImageStatus.FREE) {
                        copy.setStatus(ImageStatus.LOCK);
                        System.out.println("\t Using free");
                        ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.SUCCESS, "Start configuring");
                        return copy;
                    }
                }
                System.out.println("\t No copy is free");
                source = vmi.getImageCopies().get(0);
                final String vmName = "v" + HashGenerator.randomString(9);
                dest = new ImageCopy();
                dest.setImage(vmi);
                vmi.getImageCopies().add(dest);
                File root = new File(machineRepository + OperatingSystem.PATH_SEPARATOR + vmi.getId() + OperatingSystem.PATH_SEPARATOR + vmName);
                if (source.getMainFile().getExecutableFile().getName().contains(".")) {
                    String[] fileParts = source.getMainFile().getExecutableFile().getName().split("\\.");
                    dest.setMainFile(new File(root, vmName + "." + fileParts[fileParts.length - 1]));
                } else
                    dest.setMainFile(new File(root, vmName));
                dest.setStatus(ImageStatus.LOCK);
                saveImages();
                ServerMessageSender.reportExecutionState(execution.getId(), ExecutionProcessEnum.SUCCESS, "Start configuring");
                SystemUtils.sleep(2000);
            }
        }
        System.out.println("\tclonning");
        return source.cloneCopy(dest);
    }

    /**
     * returns or creates an image
     *
     * @param imageId image Id
     * @return desired image
     */
    private synchronized static Image getImage(long imageId) {
        loadImages();
        Image vmi = imageList.get(imageId);
        if (vmi == null) {
            vmi = new Image();
            vmi.setId(imageId);
            imageList.put(imageId, vmi);
            saveImages();
        }
        return vmi;
    }

    /**
     * Unlocks an image copy
     *
     * @param vmiCopy image copy to be free
     */
    public synchronized static void freeLockedImageCopy(ImageCopy vmiCopy) {
        System.out.println("\t break free " + vmiCopy.getMainFile().getFilePath());
        Image image = ImageCacheManager.getImage(vmiCopy.getImage().getId());
        System.out.println("The agent is retrieving the image copy " + vmiCopy.getImageName() + " of " + image.getId() + " for releasing it to compare with other image copies");
        for (ImageCopy imC : image.getImageCopies())
            if (imC.getImageName().equals(vmiCopy.getImageName()))
                imC.setStatus(ImageStatus.FREE);
        System.out.println("The agent freed the image copy " + vmiCopy.getImageName() + " ");
        saveImages();
    }

    /**
     * Removes all images fom physical machine disk
     *
     * @return operation confirmation
     */
    public static synchronized UnaCloudResponse clearCache(boolean isCopy) {
        System.out.println("clearCache");
        loadImages();

        HashMap<Long,Integer> names=new HashMap<>();
        HashMap<Long,Image> hash=new HashMap<>();
        try {
            System.out.println("The agent is clearing cache from it's image list");
            File choice=null;
            long lastMod=0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


            ArrayList<File> files= new ArrayList<>();
            //Strategy for removing most recent copy
            if (isCopy)
            {
                for(Image image:imageList.values())
                {
                    lastMod=Long.MIN_VALUE;
                    choice=null;
                    System.out.println("Image: "+image.getId());
                    for(ImageCopy copy: image.getImageCopies())
                    {
                        File file = copy.getMainFile().getExecutableFile();
                        System.out.println("\tFILE "+file.getAbsolutePath()+" "+file.lastModified()+" "+file.exists()+" "+sdf.format(file.lastModified()));
                        if (file.lastModified() > lastMod) {
                            choice = file;
                            lastMod = file.lastModified();
                        }

                    }
                    files.add(choice);
                    hash.put(image.getId(),image);
                    System.out.println("\t\tMAX "+choice.lastModified()+" "+choice.getAbsolutePath());
                }
            }
            for (Image image : imageList.values())
                for (ImageCopy copy : image.getImageCopies()) {
                    try {
                        System.out.println("\tImage: " + copy.getMainFile().getFilePath());
                        if (isCopy && files.contains(copy.getMainFile().getExecutableFile()))
                            continue;
                        System.out.println("\tRemove execution: " + copy.getMainFile().getFilePath());
                        copy.stopAndUnregister();
                        System.out.println("\tRemoving torrent: " + copy.getMainFile().getFilePath());
                        TorrentClient.getInstance().removeTorrent(copy.getMainFile().getTorrentFile());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            for (File f : new File(machineRepository).listFiles()) {
                System.out.println("\tFile: " + f.getAbsolutePath());
                System.out.println("\tDelete File: " + f.getAbsolutePath());
                //Should be FileProcessor.deleteFileSync(f.getAbsolutePath())
                deleteFileSync(f.getAbsolutePath(),files,isCopy);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("The agent is clearing its image list");
        imageList.clear();
        if (isCopy)
        {
            for (Long id: hash.keySet())
                imageList.put(id,hash.get(id));
        }
        System.out.println("The agent is saving images");
        saveImages();
        return new UnaCloudResponse(SUCCESSFUL_OPERATION, ExecutionProcessEnum.SUCCESS);
    }


    public static boolean deleteFileSync(String pathFile, ArrayList<File> forbidden, boolean isCopy) throws Exception {
        File file = new File(pathFile);
        if (!file.exists()) {
            return false;
        } else {
            System.out.println("\t File params "+isCopy +" "+file.getAbsolutePath().contains("base"));
            if(isCopy && (isCopyFile(file,forbidden) || file.getAbsolutePath().contains("base")))
                    return false;
            if (file.isDirectory()) {
                File[] var5;
                int var4 = (var5 = file.listFiles()).length;

                for(int var3 = 0; var3 < var4; ++var3) {
                    File f = var5[var3];
                    deleteFileSync(f.getAbsolutePath(),forbidden, isCopy);
                }

                System.out.println("Deletes folder: " + file.getAbsolutePath() + " " + file.delete());
            } else {
                System.out.println("Deletes file: " + file.getAbsolutePath() + " " + file.delete());
            }

            return true;
        }
    }
    private static boolean isCopyFile(File f, List<File> files) {
        String name="";
        for(File file:files)
        {
            System.out.println("Copies "+f.getAbsolutePath()+" "+file.getAbsolutePath());
            name=file.getName().replaceAll(".vbox","");
            System.out.println("File name "+name+" VS "+f.getName());
            System.out.println("Boolean "+f.getName().contains(name)+" "+f.getName().contains(name));
            if(f.getName().contains(name))
                return true;
        }
        return false;

    }




    /**
	 * Removes an image from cache in repository
	 * @return response
	 * @throws Exception 
	 */
	public static synchronized UnaCloudResponse clearImageFromCache(Long imageId) throws Exception {
		System.out.println("clearCache for image " + imageId);
		loadImages();
		Image vmi = imageList.get(imageId);		
		if (vmi != null) {
			for (ImageCopy copy : vmi.getImageCopies()) {
				try {				
					copy.stopAndUnregister();
					TorrentClient.getInstance().removeTorrent(copy.getMainFile().getTorrentFile());								
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}	
			imageList.remove(imageId);
			saveImages();
		}
		File folder = new File(machineRepository + OperatingSystem.PATH_SEPARATOR + imageId);
		System.out.println("\tDelete: " + folder);
		if (folder.exists())
			for (File root : new File(machineRepository + OperatingSystem.PATH_SEPARATOR + imageId).listFiles())
				FileProcessor.deleteFileSync(root.getAbsolutePath());
		return new UnaCloudResponse(SUCCESSFUL_OPERATION, ExecutionProcessEnum.SUCCESS)  ;
	}
	
	/**
	 * Saves the images data in a file
	 */
	private static synchronized void saveImages() {
        System.out.println("\tSaving images...");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(imageListFile))) {
			oos.writeObject(imageList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads image info from file
	 */
	@SuppressWarnings("unchecked")
	private static void loadImages() {
		System.out.println("The agent is loading the imageList");
		if (imageList == null) {
			System.out.println("The image list is empty");
			imageList = new TreeMap<>();
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(imageListFile))) {
				imageList = (Map<Long,Image>) ois.readObject();
				for (Image im : imageList.values())
				{
					System.out.println("The agent is copying image "+im.getId()+" into its list");
					for(ImageCopy copy : im.getImageCopies())
						copy.setStatus(ImageStatus.FREE);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	

	/**
	 * Deletes an image
	 * @param imageId
	 */
	public static void deleteImage(Long imageId) {
		loadImages();
		Image vmi = imageList.get(imageId);
		if (vmi != null) {
			imageList.remove(imageId);
			saveImages();
		}
	}
	
	/**
	 * Returns the list of current images 
	 * @return list of images
	 */
	public static List<Long> getCurrentImages() {
		loadImages();
		List<Long> ids = new ArrayList<Long>();
		ids.addAll(imageList.keySet());
		return ids;
	}

}
