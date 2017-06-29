package uniandes.unacloud.utils.security.net;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public abstract class SSLUnaSocket {
	
	protected int port;
	
	protected String ipAddress;
	
	protected final static String DEFAULT_PROTOCOL = "TLS";
	
	public SSLUnaSocket(int port, String ipAddress, String storeType, String keyStorePath, String password, String protocol, String algorithm,
			String trustedStoreType, String trustedKeyStorePath, String trustedPassword, String trustedAlgorithm) throws Exception {
		
		if (keyStorePath == null && trustedKeyStorePath == null) 
			throw new Exception("Key store or trusted key stored must not be null");
		
		this.port = port;
		this.ipAddress = ipAddress;
		
		KeyManager[] keyManagers = null;
		if (keyStorePath != null) {
			 KeyStore keyStore = KeyStore.getInstance(storeType);
		     keyStore.load(new FileInputStream(keyStorePath),
		            password.toCharArray());
	
		     KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
		     kmf.init(keyStore, password.toCharArray());
		     keyManagers = kmf.getKeyManagers();
		 }
	     
		 TrustManager[] trustManagers = null;
		 if (trustedKeyStorePath != null) {
			 KeyStore trustedStore = KeyStore.getInstance(trustedStoreType);
		     trustedStore.load(new FileInputStream(trustedKeyStorePath), 
		    		 trustedPassword.toCharArray());

		     TrustManagerFactory tmf = TrustManagerFactory.getInstance(trustedAlgorithm);
		     tmf.init(trustedStore);		     
		     trustManagers = tmf.getTrustManagers();
		 }	    
		 SSLContext sc = SSLContext.getInstance(protocol);
	     sc.init(keyManagers, trustManagers, null);

	     initializeSocket(sc);
	}
	
	protected abstract void initializeSocket(SSLContext context) throws Exception;

}
