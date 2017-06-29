package uniandes.unacloud.utils.security.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import uniandes.unacloud.utils.security.HashGenerator;
import uniandes.unacloud.utils.securty.exceptions.NetException;

import static uniandes.unacloud.utils.security.net.SSLProtocolKeys.*;

public class SSLUnaClientSocket extends SSLUnaSocket {
	
	private SSLSocket socket;
	
	private DataOutputStream output;
	
	private DataInputStream input;
		
	public SSLUnaClientSocket(int port, String ipAddress, String storeType, String keyStorePath, String password, String protocol, String algorithm,
			String trustedStoreType, String trustedKeyStorePath, String trustedPassword, String trustedAlgorithm) throws Exception {
		super(port, ipAddress, storeType, keyStorePath, password, protocol, algorithm, trustedStoreType, trustedKeyStorePath, trustedPassword, trustedAlgorithm);	
	}
	
	public SSLUnaClientSocket(int port, String ipAddress, String storeType, String keyStorePath, String password, String protocol, String algorithm) throws Exception {
		super(port, null, null, null, null, protocol, null, storeType, keyStorePath, password, algorithm);		
	}
	
	public SSLUnaClientSocket(int port, String ipAddress, String storeType, String keyStorePath, String password) throws Exception {
		this(port, ipAddress, storeType, keyStorePath, password, DEFAULT_PROTOCOL, KeyManagerFactory.getDefaultAlgorithm());
	}

	@Override
	protected void initializeSocket(SSLContext context) throws Exception {
		 SSLSocketFactory ssf = context.getSocketFactory();
	     socket = (SSLSocket) ssf.createSocket(ipAddress, port);
		 output = new DataOutputStream(socket.getOutputStream());
		 input = new DataInputStream(socket.getInputStream());
	}
		
	public void write(String message) throws Exception {			
		 output.writeUTF(message);
         output.flush();
	}
	
	public void writeInt(int message) throws Exception {		
		output.writeInt(message);
        output.flush();
	}
	
	public String read() throws Exception {			
		 return input.readUTF();
	}
	
	public int readInt() throws Exception {
		 return input.readInt();
	}
	
	public File readFile(int port, String path) throws Exception {
		File file = null;
		Socket s = null;
		try (ServerSocket ss = new ServerSocket(port)) {
			writeInt(READY_FOR_RECEIVE);
			if (readInt() == READY_FOR_SEND) {
				String key = read();
				s = ss.accept();
				DataInputStream dsS = new DataInputStream(s.getInputStream());
				if (key.equals(dsS.readUTF())) {
					System.out.println("Key is valid");
					String checksum = read();					
					ZipInputStream zis = new ZipInputStream(dsS);
					System.out.println("\tStart downloading file");
					final byte[] buffer = new byte[1024 * 100];	
					ZipEntry entry = zis.getNextEntry();
					file = new File(path + entry.getName());
					System.out.println("\tReceiving file: " + file);
					try (FileOutputStream fos = new FileOutputStream(file)) {
						for (int n; (n = zis.read(buffer)) != -1;)
							fos.write(buffer, 0, n);																			
					}	
					System.out.println("Finish");
					zis.closeEntry();
					zis.close();
					String checksumFile = HashGenerator.generateChecksumMD5(file);
					if (!checksum.equals(checksumFile)) {
						System.out.println();
						file.delete();
						writeInt(INVALID_FILE);
						throw new NetException("Checksum is not valid");
					}
					else 
						writeInt(RECEIVED);
				}
				else {
					dsS.close();					
					throw new NetException("Key is not valid");
				}
				s.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (s != null)
				s.close();
			throw e;
		}
		return file;
	}
	
	public void close() throws Exception {
		input.close();
		output.close();
		socket.close();
	}

}
