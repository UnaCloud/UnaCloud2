package fileTransfer;

import com.losandes.fileTransfer.Destination;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import physicalmachine.Network;

/**
 * 
 * @author Clouder
 */
public class TransferenciaArchivo {
	
	private RandomAccessFile rafarchivo;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket s;
    private long cent=0;
    private long idTransferencia;
    private ArrayList<Destination> destinos;
    boolean conected =false;
    boolean dtCnt =false;
    final long tamano;
    File archivo;
    volatile boolean enviando=false;
    
    /**
     * Class constructor
     * @param ipDestinos 
     * @param idTransferencia
     * @param nParticiones
     * @param archivo
     * @param tamano
     * @throws IOException
     */
    public TransferenciaArchivo(String[] ipDestinos,long idTransferencia,int nParticiones,File archivo,long tamano)throws IOException{
        this.archivo=archivo;
        this.idTransferencia=idTransferencia;
        destinos=crearDestinos(ipDestinos, nParticiones);
        archivo.getParentFile().mkdirs();
        archivo.delete();
        rafarchivo=new RandomAccessFile(archivo,"rw");
        this.tamano=tamano;
    }
    /**
     * Opens the file transfer agent collection
     * @param s socket with connection information
     * @param dis data input stream necessary for initialization
     */
    public void connect(Socket s,DataInputStream dis){
        if(conected)return;
        try {
            this.s = s;
            this.dis = dis;
            dos = new DataOutputStream(s.getOutputStream());
            dos.writeLong(cent);
            dos.flush();
            conected=true;
            if(!dtCnt){
                for(Destination d:destinos)d.connect();
                dtCnt=true;
            }
            recibirArchivo();
            conected=false;
        } catch (IOException ex) {
            conected=false;
        }
    }
    
    /**
     * Closes the file transfer agent connection
     */
    public void close(){
        if(conected){
            try {s.close();} catch (IOException ex) {}
            for(Destination d:destinos)d.close();
        }
        try {
            rafarchivo.close();
        } catch (IOException ex) {
        }
    }
    
    
    /**
     * Receives a file and sends it to destinations if needed
     */
    public void recibirArchivo(){
        try {
            byte[] buffer = new byte[1024*5];
            for (int e = 0; (e=dis.read(buffer,0,buffer.length)) != -1;buffer=new byte[1024*100]) {
                rafarchivo.write(buffer,0, e);
                for(Destination d:destinos)d.sendBytes(buffer, e);
                cent+=e;
                if(cent==tamano)break;
            }
            rafarchivo.close();
            String h = "";
            for(Destination d:destinos){
                h+=d.waitCompletation()+":";
                d.close();
            }
            dos.writeUTF(h+Network.getHostname());
            dos.flush();
            s.close();
        } catch (IOException ex) {
            //cerrar hijos y padre
        }
    }
    /**
     * Creates new destinations
     * @param ips new destinations IPs
     * @param nParticiones number of destinations groups
     * @return
     */
    public ArrayList<Destination> crearDestinos(String[] ips,int nParticiones){
        if(ips.length==0)return new ArrayList<Destination>();
        System.out.println("-------------- "+Arrays.toString(ips));
        String[][] grupos = new String[nParticiones][];
        int d = (ips.length-nParticiones)/(nParticiones);
        int r = (ips.length-nParticiones)%(nParticiones);
        System.out.println(ips.length+" "+d+" "+r);
        if(ips.length<nParticiones){
            for(int e=0;e<grupos.length;e++)grupos[e]=new String[0];
        }
        else{
            for(int e=0;e<r;e++)grupos[e]=new String[d+1];
            for(int e=r;e<grupos.length;e++)grupos[e]=new String[d];
            for(int e=0,j=nParticiones;e<grupos.length;e++)for(int i=0;i<grupos[e].length;i++){
                grupos[e][i]=ips[j];
                j++;
            }
        }
        ArrayList<Destination> dest = new ArrayList<Destination>(Math.min(nParticiones,ips.length));
        for(int e=0,i=Math.min(nParticiones,ips.length);e<i;e++)dest.add(new Destination(ips[e],grupos[e], idTransferencia));
        return dest;
    }

}
