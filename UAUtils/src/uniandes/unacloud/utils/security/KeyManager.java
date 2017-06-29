package uniandes.unacloud.utils.security;

import uniandes.unacloud.utils.LocalProcessExecutor;

/**
 * Responsible class to manage Key stores 
 * This class provides methods to generate key stores and public keys, and add trusted certifies to key stores
 * @author CesarF
 *
 */
public class KeyManager {
	
	/**
	 * Generates a new keyStore based in parameters
	 * @param algorithm encryption algorithm for key store
	 * @param alias name for private key located in key store
	 * @param keyStorePath path where key store will be stored (store name is necessary)
	 * @param provider name for certificate
	 * @param organizationUnit Unit which issues certificate
	 * @param organization which issues certificate
	 * @param location of organization
	 * @param country of organization
	 * @param password to access keyStore
	 */
	public static void generateKeyStore (String algorithm, String alias, String keyStorePath, 
			String provider, String organizationUnit, String organization, String location, String country, String password) {
		
		String[] command = new String[] {
				"keytool", "-genkey", 
				"-keyalg ", algorithm, 
				"-alias", alias, 
				"-keystore", keyStorePath,
				"-dname", "\"CN=" + provider + ", OU=" + organization + ", O=" + organization + ", L=" + location +" C=" + country + "\"",
				"-storepass", password,
				"-keypass",password
				};
		LocalProcessExecutor.executeCommand(command);
	}
	
	/**
	 * Generates a public key/certificate from a private key in store
	 * @param keyStorePath path where is located key store (store name is necessary)
	 * @param alias name for private key in store
	 * @param keyPath path where certificate will be exported (certified name is necessary)
	 * @param password to access keyStore
	 */
	public static void generatePublicKey (String keyStorePath, String alias, String keyPath, String password) {
		
		String[] command = new String[] {
				"keytool", "-export", 
				"-keystore", keyStorePath,
				"-alias", alias, 
				"-file", keyPath, 
				"-storepass", password
				};
		LocalProcessExecutor.executeCommand(command);
	}
	
	/**
	 * Adds a trusted key/certificate to key store 
	 * @param alias name for public key
	 * @param keyPath path where is located public key (certified name is necessary)
	 * @param keyStorePath path where is located key store (store name is necessary)
	 * @param password to access key store 
	 */
	public static void addTrustedCerts (String alias, String keyPath, String keyStorePath, String password) {
		
		String[] command = new String[] {
				"keytool", "-import", 
				"-alias", alias,
				"-file", keyPath, 
				"-keystore", keyStorePath, 
				"-keypass", password,
				"-storepass", password, 
				"-noprompt"
				};
		LocalProcessExecutor.executeCommand(command);
	}

}
