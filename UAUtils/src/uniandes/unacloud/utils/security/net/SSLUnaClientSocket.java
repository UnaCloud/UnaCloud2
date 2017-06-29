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


public class SSLUnaClientSocket extends SSLUnaSocket {
	
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
	
	public File readFile(int port) throws Exception {
		File file = null;
		Socket s = null;
		try (ServerSocket ss = new ServerSocket(port)) {
			writeInt(1);
			if (readInt() == 2) {
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
					file = File.createTempFile(entry.getName(), null);
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
						writeInt(4);
						throw new NetException("Checksum is not valid");
					}
					else 
						writeInt(3);
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
