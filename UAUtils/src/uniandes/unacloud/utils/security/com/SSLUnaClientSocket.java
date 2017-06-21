package uniandes.unacloud.utils.security.com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class SSLUnaClientSocket extends SSLUnaSocket{
	
	private SSLSocket socket;
	private DataOutputStream output;
	private DataInputStream input;
		
	public SSLUnaClientSocket(int port, String ipAddress, String storeType, String keyStorePath, String password, String protocol, String algorithm,
			String trustedStoreType, String trustedKeyStorePath, String trustedPassword, String trustedProtocol, String trustedAlgorithm) throws Exception {
		super(port, ipAddress, storeType, keyStorePath, password, protocol, algorithm, trustedStoreType, trustedKeyStorePath, trustedPassword, trustedProtocol, trustedAlgorithm);	
	}
	
	public SSLUnaClientSocket(int port, String ipAddress, String storeType, String keyStorePath, String password, String protocol, String algorithm) throws Exception {
		super(port, null, null, null, null, null, null, storeType, keyStorePath, password, protocol, algorithm);		
	}
	
	public SSLUnaClientSocket(int port, String ipAddress, String storeType, String keyStorePath, String password) throws Exception {
		this(port, ipAddress, storeType, keyStorePath, password, "TLS", KeyManagerFactory.getDefaultAlgorithm());
	}

	@Override
	protected void initializeSocket(SSLContext context) throws Exception {
		 SSLSocketFactory ssf = context.getSocketFactory();
	     socket = (SSLSocket) ssf.createSocket(ipAddress, port);
	}
		
	public void write(String message) throws Exception {	
		if (output == null)
			output = new DataOutputStream(socket.getOutputStream());
		 output.writeUTF(message);
         output.flush();
	}
	
	public void writeInt(int message) throws Exception {		
		if (output == null)
			output = new DataOutputStream(socket.getOutputStream());
		output.writeInt(message);
        output.flush();
	}
	
	public String read() throws Exception {
		if (input == null)
			input = new DataInputStream(socket.getInputStream());
		 return input.readUTF();
	}
	
	public int readInt() throws Exception {
		if (input == null)
			input = new DataInputStream(socket.getInputStream());
		 return input.readInt();
	}
	
	public File readFile(int port) throws Exception {
		File file = null;
		try (ServerSocket ss = new ServerSocket(port); 
				Socket s = ss.accept(); 
				ZipInputStream zis = new ZipInputStream(s.getInputStream())) {
				
				System.out.println("\tZip open");
				final byte[] buffer = new byte[1024 * 100];			
				
				for (ZipEntry entry; (entry = zis.getNextEntry()) != null;) {
					boolean goodExtension = false;
					String mainExtension = null;
					System.out.println("\t\tFile: " + entry.getName());
					
					File tempFile = File.createTempFile(entry.getName(), null);
					try (FileOutputStream fos = new FileOutputStream(tempFile)) {
						for (int n; (n = zis.read(buffer)) != -1;) {
							fos.write(buffer, 0, n);
						}
						if (entry.getName().endsWith(mainExtension)){
							newMainFile=mainFile + entry.getName();								
						}								
					}	
					System.out.println("Save temp "+tempFile);
					filesTemp.put(tempFile,entry.getName());
					tempFile.deleteOnExit();
					zis.closeEntry();
				}
				System.out.println("There are "+filesTemp.size()+", Delete old files");
				Long sizeImage= 0l;
				
				if (filesTemp.size() > 0) {			
					
					File dir = new File(mainFile);
					System.out.println(dir + " " + dir.exists());
					System.out.println("Creates: " + dir.mkdirs());
					for (java.io.File f:dir.listFiles())
						if (f.isFile())
							f.delete();						
					System.out.println("Save new files");
					
					for (File temp : filesTemp.descendingKeySet()) {
						File newFile = new File(mainFile, filesTemp.get(temp));
						//newFile.createNewFile();
						System.out.println("save: "+newFile);
						try (FileInputStream streamTemp = new FileInputStream(temp); FileOutputStream ouFile = new FileOutputStream(newFile)) {
							for (int n; (n = streamTemp.read(buffer)) != -1;) {
								ouFile.write(buffer, 0, n);
							}															
						}	
						
						sizeImage += temp.length();
						temp.delete();
					}
				}	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public void close() throws Exception {
		input.close();
		output.close();
		socket.close();
	}

}
