package communication.security.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.Cipher;

import communication.UnaCloudMessage;
import static com.losandes.utils.Constants.*;

/**
 * Security abstract class that is responsible for writing and reading encripted data on a given socket
 * @author Clouder
 */
public abstract class AbstractCommunicator {

    /**
     * Socket used to write and read encrypted data
     */
    protected Socket socket;

    /**
     * Data output stream constructed to write data on managed socket
     */
    protected DataOutputStream dataOutput;

    /**
     * Data output stream constructed to read data from managed socket
     */
    protected DataInputStream dataInput;

    /**
     * Cipher object used to encrypt and decrypt data on socket
     */
    protected Cipher cipher;

    /**
     * Ke used on asymmetric encryption algorithm
     */
    protected Key key;

    /**
     * True if this channel is a encrypted channel, otherwise false
     */
    protected boolean cifrado=true;

    public AbstractCommunicator(){
    }
    /**
     * Responsible for connecting with Clouder Server using the Clouder Client client socket
     */
    public void connect(Socket s)throws ConnectionException{
        socket = s;
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
            dataInput = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            close();
            throw new ConnectionException("Unable to create data streams.");
        }
    }

    /**
     * Responsible for disconnecting of Clouder Server using the Clouder Client client socket
     */
    public void close(){
        try {
            if(dataOutput!=null)dataOutput.close();
        } catch (Exception ex) {
        }try{
            if(dataInput!=null)dataInput.close();
        } catch (Exception ex) {
        }try{
            if(socket!=null)socket.close();
        }catch(Exception ex){
            
        }
        dataOutput=null;
        dataInput=null;
        socket=null;
    }

    /**
     * Takes an array of Strings and write them on the socket outputstream
     * @param msg The message to be written
     * @throws ConnectionException Throws an exception if there is an error writing the message
     */
    public void writeUTF(String ... msg)throws ConnectionException{
        writeString(makeMessage(msg));
    }
    public void writeUTF(Object ... msg)throws ConnectionException{
        if(msg==null)return;
        String[] par=new String[msg.length];
        for(int e=0;e<par.length;e++)par[e]=""+msg[e];
        writeUTF(par);
    }

    /**
     * Read a string message from the socket input stream
     * @return The next message from the server
     * @throws ConnectionException Throws an exception if there is an error reading the message
     */
    public String readUTF()throws ConnectionException{
         return readString();
    }

    /**
     * Reads a message from the socket input stream and splits it
     * @return An array containing the next message from the server
     * @throws ConnectionException Throws an exception if there is an error reading the message
     */
    public UnaCloudMessage readUTFList()throws ConnectionException{
         return new UnaCloudMessage(readString().split(MESSAGE_SEPARATOR_TOKEN));
    }

    /**
     * Reads the next string from the server
     * @return The next string from the server
     * @throws ConnectionException Throws an exception if there is an error reading the message
     */
    private String readString()throws ConnectionException{
        try {
            if(cifrado){
                cipher.init(Cipher.DECRYPT_MODE, key);
                int n = dataInput.readInt();
                String ret="";
                for(int e=0;e<n;e++){
                    byte[] cipherText = new byte[dataInput.readInt()];
                    dataInput.readFully(cipherText, 0, cipherText.length);
                    byte[] plainText = cipher.doFinal(cipherText);
                    ret+=new String(plainText);
                }

                return ret;
            }else{
                int n = dataInput.readInt();
                byte[] cipherText = new byte[n];
                dataInput.readFully(cipherText, 0, cipherText.length);
                return new String(cipherText);
            }
        } catch (Exception ex){
            close();
            ex.printStackTrace();
            throw new ConnectionException("Unable to read msg. "+ex.getLocalizedMessage());

        }

    }

    /**
     * Writes a message to UnaCloud server using the socket contained in this object
     * @param line The msssage to be written
     * @throws ConnectionException Throws an exception if there is an error writing the message
     */
    private void writeString(String line)throws ConnectionException{
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException ex) {
            throw new ConnectionException("Unable to init cipher.");
        }
        byte[] h=line.getBytes();
        int n = (h.length-1)/cipher.getBlockSize()+1,t=cipher.getBlockSize();
        try {
            if(cifrado){
                
                dataOutput.writeInt(n);
                for(int e=0;e<n;e++){
                    byte[] cipherText = cipher.doFinal(h,e*t,(h.length-(e+1)*t)<0?h.length-e*t:t);
                    dataOutput.writeInt(cipherText.length);
                    dataOutput.write(cipherText);
                }
            }else{
                dataOutput.writeInt(h.length);
                dataOutput.write(h);
            }
            dataOutput.flush();
        } catch (Exception ex) {
            close();
            ex.printStackTrace();
            throw new ConnectionException("Unable to send message." +ex.getLocalizedMessage());
        }

    }

    /**
     * Converts an array of Strings into a writable message
     * @param args The array to be converted
     * @return The message generated from the array
     */
     private String makeMessage(String ... args){
        String resp = "";
        int e=0;
        while(args[e]==null&&e<args.length)e++;
        if(args.length!=e)resp=args[e];
        for(e++;e<args.length;e++)if(args[e]!=null)resp+=MESSAGE_SEPARATOR_TOKEN+args[e];
        return resp;
    }
}
