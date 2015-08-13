package communication.security.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Client side implementation of secure stream. This class implements the methods exposed on AbstractCommunicator using client side keys for encryption.
 * @author Clouder
 */
public class SecureClientStream extends AbstractCommunicator {

    /**
     * Modulus and exponent used on data encryption
     */
    private static String modulus="11986832794202572811",exponent="65537";
    static{
        try{
            BufferedReader br = new BufferedReader(new FileReader("secretsPublic.txt"));
            modulus=br.readLine();
            exponent=br.readLine();
            br.close();
        }catch(Exception e){

        }
    }
    /**
     * Sets the client key to encrypt and decrypt data
     * @param mod New RSA modulus client key
     * @param exp New RSA exponent client key
     * @return
     */
    public static boolean setKeys(String mod,String exp){
        try{
            PrintWriter pw = new PrintWriter("secretsPublic.txt");
            pw.println(mod);
            pw.println(exp);
            pw.close();
            modulus=mod;
            exponent=exp;
            return true;
        }catch(Exception e){

        }
        return false;
    }

    /**
     * Constructs a secure client stream to send secure data
     * @throws ConnectionException
     */
    public SecureClientStream() throws ConnectionException {
        try {
            Security.addProvider(new BouncyCastleProvider());
            RSAPublicKeySpec privKeySpec = new RSAPublicKeySpec(new BigInteger(modulus, 10), new BigInteger(exponent, 10));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            key = (RSAPublicKey) keyFactory.generatePublic(privKeySpec);
        } catch (Exception ex) {
            throw new ConnectionException("Unable to create key");
        }
        try {
            cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
        } catch (Exception ex) {
            throw new ConnectionException("Unable to create cipher");
        }
    }

}
    

