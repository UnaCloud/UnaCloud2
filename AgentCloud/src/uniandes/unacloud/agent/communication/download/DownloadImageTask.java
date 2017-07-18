package uniandes.unacloud.agent.communication.download;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import udt.UDTClient;
import udt.UDTInputStream;
import udt.UDTServerSocket;
import udt.UDTSocket;
import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.communication.torrent.TorrentClient;
import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.entities.Image;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.ImageStatus;
import uniandes.unacloud.agent.system.OperatingSystem;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.utils.LocalProcessExecutor;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.utils.file.Zipper;

/**
 * Class responsible for manage download files process from server
 * @author CesarF
 *
 */
public class DownloadImageTask {
	
	private static final int FTP = 1;
	private static final int SMB = 2;
	private static final int TCP = 3;
	private static final int P2P = 4;
	private static final int UDT = 5;
	
	/**
	 * Creates a new image copy requesting images from server
	 * @param image base image
	 * @param copy empty copy
	 * @throws Exception 
	 */
	public static void dowloadImageCopy(Image image,ImageCopy copy,String repository, int tipo) throws Exception {
		File root=new File(repository+OperatingSystem.PATH_SEPARATOR+image.getId()+OperatingSystem.PATH_SEPARATOR+"base");
		ImageCacheManager.cleanDir(root);
		root.mkdirs();
		final int puerto = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.FILE_SERVER_PORT);
		final String ip=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.FILE_SERVER_IP);
		System.out.println("Connecting to "+ip+":"+puerto);
		String fileName = null;
		String serverPath = null;
		long size = 0;
		String zipFile = null;
//		int totalFiles = 1;
//		int portOpen = 0;
//		int portUDT = 0;
		try(Socket s=new Socket(ip,puerto);DataOutputStream ds=new DataOutputStream(s.getOutputStream()); DataInputStream dis = new DataInputStream(s.getInputStream())) {
			
			//Sends operation type ID
			System.out.println("Successful connection");
			System.out.println("Operation type 1");
			ds.writeInt(UnaCloudConstants.REQUEST_IMAGE);
			ds.flush();
			
			//sends image id
			System.out.println("send ID "+image.getId());
			ds.writeLong(image.getId());
			ds.flush();
			
			size = dis.readLong();
			
			zipFile = dis.readUTF();
			
			ds.writeInt(tipo);
						
			switch (tipo) {							
				case TCP:
					fileName =  downloadByTCP(root, s);
					break;					
				case P2P:
					fileName = downloadByTCPDeployTorrent(root, s, image, copy);
					break;
				case FTP:
					fileName = downloadByTCP(root, s);
					break;
				case SMB:
					serverPath = dis.readUTF();
					break;
//				case UDT:
//					totalFiles = dis.readInt();
//					portUDT = dis.readInt();
//					portOpen = dis.readInt();
//					break;
			}			
			
		} catch (Exception e) {
			throw new ExecutionException("Error opening connection",e);
		}
		//long totalLUDP = 0;
		switch (tipo) {
			case FTP:
				//FAILED
				downloadByFTP(root, fileName);
			break;					
			case SMB:
				//FAILED
				downloadBySMB(serverPath,zipFile,root);		
			break;					
			case P2P:
				//WORKS
				downloadByP2P(root, fileName);
				copy.setImage(image);
				image.getImageCopies().add(copy);
				copy.init();	
			break;
		}
		if (tipo == P2P || tipo == TCP) {		
			boolean imageIsOk = validate(root, size, zipFile);
			if (!imageIsOk)
				throw new ExecutionException("ERROR: Finish test protocol "+tipo+" - "+imageIsOk);
			else
				ServerMessageSender.reportExecutionState(image.getId(), ExecutionStateEnum.CONFIGURING, "Download finished "+imageIsOk+ " - " + tipo);
		} else {			
			boolean imageIsOk = validateAndClean(root, size, zipFile);
			throw new ExecutionException("Finish test protocol "+tipo+" - "+imageIsOk);
		}
		
	}
	
	private static boolean validateAndClean(File root, long size, String zipFile) {
		boolean result = validate(root, size, zipFile);
		ImageCacheManager.cleanDir(root);
		return result;
	}
	
	private static boolean validate(File root, long size, String zipFile) {
		File f = new File(root.getAbsolutePath()+OperatingSystem.PATH_SEPARATOR+zipFile);
		if(size == f.length()) return true;
		return false;
	}

	
	private static String downloadByTCP(File root, Socket s) {
		String fileName = null;
		System.out.println("Download seed");
		try(ZipInputStream zis=new ZipInputStream(s.getInputStream())){
			System.out.println("Zip open");
			byte[] buffer=new byte[1024*100];
			for(ZipEntry entry;(entry=zis.getNextEntry())!=null;) {				
				try(FileOutputStream fos=new FileOutputStream(new File(root,entry.getName()))){
					fileName = entry.getName();
					for(int n;(n=zis.read(buffer))!=-1;){
						fos.write(buffer,0,n);
					}						
				}
				zis.closeEntry();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return fileName;
	}
	
	private static String downloadByTCPDeployTorrent(File root, Socket s, Image image, ImageCopy copy) {
		String fileName = null;
		System.out.println("Download seed");
		try(ZipInputStream zis=new ZipInputStream(s.getInputStream())){
			System.out.println("Zip open");
			byte[] buffer=new byte[1024*100];
			for(ZipEntry entry;(entry=zis.getNextEntry())!=null;) {				
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
						if(entry.getName().contains(".torrent")) fileName = entry.getName();
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
		return fileName;
	}

	private static void downloadByP2P(File root, String torrentName) throws Exception {
		System.out.println("Start P2P");
		new TorrentClient().downloadTorrent(root.getAbsolutePath()+OperatingSystem.PATH_SEPARATOR+torrentName, root.getAbsolutePath());			
		String zipName = (root.getAbsolutePath()+OperatingSystem.PATH_SEPARATOR+torrentName).replace(".torrent", "");
		Zipper.unzipIt(new File(zipName), root.getAbsolutePath());				
	}

	private static void downloadByFTP(File root, String file) {
		try {
			System.out.println("Start FTP");
			LocalProcessExecutor.executeCommand(new String[]{"ftp","-s:"+root.getAbsolutePath()+OperatingSystem.PATH_SEPARATOR+file});		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void downloadBySMB(String path, String file, File root ) {
		try {
			System.out.println("Start SMB");
			//{"net","use","X:","\\\\157.253.195.56\\Cursos"});
			LocalProcessExecutor.executeCommand(new String[]{"net","use","X:",path});
			//"xcopy","X:\\fz.exe"
			LocalProcessExecutor.executeCommand(new String[]{"xcopy","X:\\"+file,root.getAbsolutePath()});	
			LocalProcessExecutor.executeCommand(new String[]{"net","use","X:","/delete"});	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
