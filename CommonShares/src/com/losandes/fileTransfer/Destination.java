package com.losandes.fileTransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

/**
 * Class that represents a participant of a Tree based file distribution
 * @author Clouder
 */
public class Destination {

    /**
     * The port to be used to contact this destination
     */
    public static int FILE_TRANSFER_SOCKET;

    /**
     * The ip address for this destination
     */
    private String ipDestino;
    /**
     * Output stream used to send data for this destination
     */
    private DataOutputStream outFile;
    /**
     * Input stream used to read data from this destination
     */
    private DataInputStream inFile;

    /**
     * The tree nodes that this destination is responsible for
     */
    private String[] hijos;

    /**
     * Socket used to communicate with this destination
     */
    private Socket s;
    /**
     * Id of the file transfer
     */
    private long id;
    /**
     * Sentinel used to mark how many bytes were readed from the input stream
     */
    private long cent;
    /**
     * boolean variable that tells if this destination is connected or not
     */
    private boolean conectado=true;

    /**
     * Creates a Destination given its ip address and the childs under its charge
     * @param ipDestino
     * @param hijos
     * @param id
     */
    public Destination(String ipDestino, String[] hijos,long id) {
        this.ipDestino = ipDestino;
        this.hijos = hijos;
        this.id=id;
    }

    /**
     * Connects to this destination
     */
    public void connect(){
        try {
            s = new Socket(ipDestino,FILE_TRANSFER_SOCKET);
            outFile = new DataOutputStream(new AsynchronusOutputStream(s.getOutputStream(),100));
            inFile=new DataInputStream(s.getInputStream());
            outFile.writeLong(id);
            outFile.flush();
            cent = inFile.readLong();
            conectado=true;
        } catch (UnknownHostException ex){
            ex.printStackTrace();
            conectado=false;
        } catch (IOException ex) {
            ex.printStackTrace();
            conectado=false;
        }
    }

    /**
     * Closes the connection with this destination
     */
    public void close(){
        conectado=false;
        try {
            if(outFile!=null)outFile.close();
        } catch (IOException ex) {
            Logger.getLogger(Destination.class.getName()).log(Level.SEVERE, null, ex);
        }
        //try{s.close();}catch(Exception e){}
    }

    /**
     * Recovers the state of this destination
     * @param centActual
     * @param file
     * @throws IOException
     */
    public void recuperar(long centActual,RandomAccessFile file)throws IOException{
        byte[] buffer = new byte[1024*10];
        while(cent<centActual){
            file.seek(cent);
            long h=centActual-cent;
            while(h!=0){
                int i = file.read(buffer,0,h>buffer.length?buffer.length:(int)h);
                try{
                    outFile.write(buffer,0,i);
                }catch(IOException ex){
                    conectado=false;
                }
                h-=i;
            }
        }
    }

    /**
     * Send the given array to this destination
     * @param b
     * @param l
     */
    public void sendBytes(byte[] b,int l){
        if(!conectado)return;
        try {
            outFile.write(b, 0, l);
        } catch (IOException ex) {
            try{
                outFile.close();
            }catch (IOException ex2){}
            conectado=false;
        }
    }

    /**
     * Returns if this destination is connected or not
     * @return
     */
    public boolean isConectado() {
        return conectado;
    }

    /**
     * Return the assigned childs of this destination
     * @return
     */
    public String[] getHijos() {
        return hijos;
    }

    /**
     * waits for this destination to end its file transfer
     * @return
     */
    public String waitCompletation(){
        try{
            return inFile.readUTF();
        }catch(Exception e){
            return "";
        }
    }

    /**
     * 
     * @return
     */
    public String getIpDestino() {
        return ipDestino;
    }

    @Override
    public String toString() {
        return "Deestino "+ipDestino+" "+Arrays.toString(hijos);
    }



    
}
