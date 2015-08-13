package fileTransfer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;

import com.losandes.utils.VariableManager;
/**
 * This class is responsible for managing Tree Based File Transfer channels.
 * @author Clouder
 */
public class TreeDistributionChannelManager {

    /**
     * A map that contains returns a chnannel given its long id
     */
    static final TreeMap<Long,TransferenciaArchivo> transfers = new TreeMap<Long, TransferenciaArchivo>();

    /**
     * Socket used to attend incoming channel requests
     */
    private ServerSocket ss;

    /**
     * Constructor of class TreeDistributionChannelManager. This constructors starts a Thread that listens on a local port.
     */
    public TreeDistributionChannelManager() {
        new Thread(){
            @Override
            public void run() {
                try {
                    ss = new ServerSocket(VariableManager.global.getIntValue("FILE_TRANSFER_SOCKET"));
                    while(true){
                        Socket s = ss.accept();
                        DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream(),1024*100));
                        long id = dis.readLong();
                        TransferenciaArchivo T = transfers.get(id);
                        System.out.println("Tranfer "+id+" "+T);
                        T.connect(s, dis);
                    }
                } catch (IOException ex) {
                }
            }
        }.start();

    }


}
