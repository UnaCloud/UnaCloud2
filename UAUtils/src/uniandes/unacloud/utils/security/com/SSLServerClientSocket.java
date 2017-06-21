package uniandes.unacloud.utils.security.com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uniandes.unacloud.utils.security.HashGenerator;
import uniandes.unacloud.utils.securty.exceptions.CommException;

public class SSLServerClientSocket {

	private Socket comSocket;
	private DataOutputStream output;
	private DataInputStream input;
		
	public SSLServerClientSocket(Socket socket) throws Exception {
		this.comSocket = socket;
	}
	
	public void sendFile(File file, int port) throws Exception {
		if (output == null)
			output = new DataOutputStream(comSocket.getOutputStream());
		if (input == null)
			input = new DataInputStream(comSocket.getInputStream());
		String checksum = HashGenerator.generateChecksumMD5(file);
		try (Socket fileSocket = new Socket(comSocket.getInetAddress(), port);) {
			output.writeLong(file.getTotalSpace());
			output.flush();
			int resp = input.readInt();
			if (resp == 0) throw new CommException("Client responses it can't receive file");
			
			ZipOutputStream zos = new ZipOutputStream(fileSocket.getOutputStream());
			{
				System.out.println("\tSending file: " + file);
				final byte[] buffer = new byte[1024*100];
				
				zos.putNextEntry(new ZipEntry(file.getName()));
							
				try (FileInputStream fis = new FileInputStream(file)) {
					for(int n; (n = fis.read(buffer)) != -1;)
						zos.write(buffer,0,n);
				}
				zos.closeEntry();			
				System.out.println("File sent");	
			}						
			zos.flush();		
			output.writeUTF(checksum);
			resp = input.readInt();
			if (resp == 0) throw new CommException("Client responses File is not valid");
		} catch (Exception e) {
			//it is necessary because we need to close filesocket 
			throw e;
		}
	}
	
	public void write(String message) throws Exception {	
		if (output == null)
			output = new DataOutputStream(comSocket.getOutputStream());
		 output.writeUTF(message);
         output.flush();
	}
	
	public void writeInt(int message) throws Exception {		
		if (output == null)
			output = new DataOutputStream(comSocket.getOutputStream());
		output.writeInt(message);
        output.flush();
	}
	
	public String read() throws Exception {
		if (input == null)
			input = new DataInputStream(comSocket.getInputStream());
		 return input.readUTF();
	}
	
	public int readInt() throws Exception {
		if (input == null)
			input = new DataInputStream(comSocket.getInputStream());
		 return input.readInt();
	}
	
	public void close() throws Exception {
		input.close();
		output.close();
		comSocket.close();
	}

}
