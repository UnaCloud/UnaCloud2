package uniandes.unacloud.utils.security.net;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLUnaServerSocket extends SSLUnaSocket {
	
	private SSLServerSocket serverSocket;
		
	public SSLUnaServerSocket (int port, String storeType, String keyStorePath, String password, String protocol, String algorithm,
			String trustedStoreType, String trustedKeyStorePath, String trustedPassword, String trustedProtocol, String trustedAlgorithm) throws Exception {
		super(port, null, storeType, keyStorePath, password, protocol, algorithm, trustedStoreType, trustedKeyStorePath, trustedPassword, trustedAlgorithm);		
	}
	
	public SSLUnaServerSocket (int port, String storeType, String keyStorePath, String password, String protocol, String algorithm) throws Exception {
		super(port, null, storeType, keyStorePath, password, protocol, algorithm, null, null, null, null);		
	}
	
	public SSLUnaServerSocket(int port, String storeType, String keyStorePath, String password) throws Exception {
		this(port, storeType, keyStorePath, password, DEFAULT_PROTOCOL, KeyManagerFactory.getDefaultAlgorithm());
	}

	@Override
	protected void initializeSocket(SSLContext context) throws Exception {
		SSLServerSocketFactory ssf = context.getServerSocketFactory();
	    serverSocket = (SSLServerSocket) ssf.createServerSocket(port);		
	}
	
	public SSLUnaServerClientSocket acceptClient() throws Exception {
		Socket client = serverSocket.accept();
		SSLUnaServerClientSocket clientServer = new SSLUnaServerClientSocket(client);
		return clientServer;
	}
	
	public void close() throws IOException {
		serverSocket.close();
	}

}
