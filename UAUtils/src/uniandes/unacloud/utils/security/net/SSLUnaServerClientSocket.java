package uniandes.unacloud.utils.security.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.utils.security.HashGenerator;
import uniandes.unacloud.utils.securty.exceptions.NetException;
import static uniandes.unacloud.utils.security.net.SSLProtocolKeys.*;

public class SSLUnaServerClientSocket {

	private Socket comSocket;
	private DataOutputStream output;
	private DataInputStream input;
		
	public SSLUnaServerClientSocket(Socket socket) throws Exception {
		this.comSocket = socket;
		output = new DataOutputStream(comSocket.getOutputStream());
		input = new DataInputStream(comSocket.getInputStream());
	}
	
	public void sendFile(File file, int port) throws Exception {
		
		if (readInt() == READY_FOR_RECEIVE) {
			writeInt(READY_FOR_SEND);
			String key = HashGenerator.randomString(30);
			write(key);			
			System.out.println("Start communication with " + comSocket.getInetAddress() + " - " + port);
			try (Socket fileSocket = new Socket(comSocket.getInetAddress(), port); 
					DataOutputStream dOs = new DataOutputStream(fileSocket.getOutputStream());) {							
				dOs.writeUTF(key);
				String checksum = HashGenerator.generateChecksumMD5(file);
				write(checksum);										
				ZipOutputStream zos = new ZipOutputStream(dOs);
				{
					System.out.println("\tSending file: " + file);
					final byte[] buffer = new byte[1024 * 100];				
					zos.putNextEntry(new ZipEntry(file.getName()));							
					try (FileInputStream fis = new FileInputStream(file)) {
						for (int n; (n = fis.read(buffer)) != -1;)
							zos.write(buffer, 0, n);
					}							
					System.out.println("File sent");	
				}						
				zos.flush();	
				zos.closeEntry();
				if (readInt() == INVALID_FILE) 
					throw new NetException("Client responses file is not valid");
			} catch (Exception e) {
				//it is necessary because we need to close filesocket 
				throw e;
			}
		}		
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
	
	public void close() throws Exception {
		input.close();
		output.close();
		comSocket.close();
	}

}
