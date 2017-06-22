package uniandes.unacloud.agent.communication.download;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import uniandes.unacloud.agent.communication.torrent.TorrentClient;
import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.entities.Image;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.ImageStatus;
import uniandes.unacloud.agent.system.OperatingSystem;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.common.utils.Zipper;

/**
 * Class responsible for manage download files process from server
 * @author CesarF
 *
 */
public class DownloadImageTask {
	
	/**
	 * Creates a new image copy requesting images from server
	 * @param image base image
	 * @param copy empty copy
	 * @throws Exception 
	 */
	public static void dowloadImageCopy(Image image,ImageCopy copy,String repository, String requester) throws Exception {
		File root=new File(repository+OperatingSystem.PATH_SEPARATOR+image.getId()+OperatingSystem.PATH_SEPARATOR+"base");
		ImageCacheManager.cleanDir(root);
		root.mkdirs();
		final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT);
		final String ip=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
		System.out.println("Connecting to "+ip+":"+puerto);
		String torrentName = null;
		try(Socket s=new Socket(ip,puerto);DataOutputStream ds=new DataOutputStream(s.getOutputStream())) {
			
			//Sends operation type ID
			System.out.println("Successful connection");
			System.out.println("Operation type 1");
			ds.writeInt(UnaCloudConstants.REQUEST_IMAGE);
			ds.flush();
			
			//sends image id
			System.out.println("send ID "+image.getId());
			ds.writeLong(image.getId());
			ds.flush();
			
			System.out.println("Request: "+requester);
			ds.writeUTF(requester);
			ds.flush();
			
			//Receives zip elements
			try(ZipInputStream zis=new ZipInputStream(s.getInputStream())){
				System.out.println("Zip open");
				byte[] buffer=new byte[1024*100];
				for(ZipEntry entry;(entry=zis.getNextEntry())!=null;){
					if(entry.getName().equals("unacloudinfo")){
						
						BufferedReader br=new BufferedReader(new InputStreamReader(zis));
						image.setPlatformId(br.readLine());
						System.out.println("Platform: "+image.getPlatformId());
						String mainFile=br.readLine();
						if(mainFile==null){
							throw new ExecutionException(UnaCloudConstants.ERROR_MESSAGE+" image mainFile is null");
						}
						
						copy.setMainFile(new File(root,mainFile));
						System.out.println("Main: "+mainFile);
						image.setPassword(br.readLine());
						image.setUsername(br.readLine());
						copy.setStatus(ImageStatus.LOCK);
						/*copy.setVirtualMachineName();*/br.readLine();
						image.setConfiguratorClass(br.readLine());
						System.out.println("config: "+image.getConfiguratorClass());
						
					}else{
						try(FileOutputStream fos=new FileOutputStream(new File(root,entry.getName()))){
							if(entry.getName().contains(".torrent")) torrentName = entry.getName();
							for(int n;(n=zis.read(buffer))!=-1;){
								fos.write(buffer,0,n);
							}						
						}
					}
					zis.closeEntry();
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			throw new ExecutionException("Error opening connection",e);
		}
		try {
			if (torrentName != null) {			
				new TorrentClient().downloadTorrent(repository+OperatingSystem.PATH_SEPARATOR+image.getId()+OperatingSystem.PATH_SEPARATOR+"base"+OperatingSystem.PATH_SEPARATOR+torrentName, root.getAbsolutePath());			
				String zipName = (repository+OperatingSystem.PATH_SEPARATOR+image.getId()+OperatingSystem.PATH_SEPARATOR+"base"+OperatingSystem.PATH_SEPARATOR+torrentName).replace(".torrent", "");
				Zipper.unzipIt(new File(zipName), repository+OperatingSystem.PATH_SEPARATOR+image.getId()+OperatingSystem.PATH_SEPARATOR+"base");
			}
			copy.setImage(image);
			image.getImageCopies().add(copy);
			copy.init();
		} catch (Exception e) {
			throw e;
		}
	}
}
