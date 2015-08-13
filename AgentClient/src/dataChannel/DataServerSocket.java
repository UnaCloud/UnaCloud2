/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataChannel;

import com.losandes.fileTransfer.Destination;
import com.losandes.utils.ByteUtils;
import com.losandes.utils.VariableManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Data ServerSocket used to listen data transfers requests. A data transfer request is identified by an unique id that is used to bind socket to logical data transfers. This Id is send by requesting end point as the first 8 bytes of the accepted connection input stream.
 * @author Clouder
 */
public class DataServerSocket {

    /**
     * Server socket to be used by thus DataServerSocket
     */
    private static ServerSocket ss;

    /**
     * Map to be used to assign unique IDs to DataSockets
     */
    private static Map<Long, Socket> map = Collections.synchronizedMap(new TreeMap<Long, Socket>());

    /**
     * Random generator used to create DataSockets IDs
     */
    private static Random r = new Random();

    public static void init() {
        try {
            if (ss == null) {
                ss = new ServerSocket(VariableManager.global.getIntValue("DATA_SOCKET"));
                Destination.FILE_TRANSFER_SOCKET=VariableManager.global.getIntValue("FILE_TRANSFER_SOCKET");
                new Thread() {

                    @Override
                    public void run() {
                        c:
                        while (true) {
                            try {
                                Socket s = ss.accept();
                                InputStream is = s.getInputStream();
                                byte[] bytes = new byte[8];
                                for (int r = 0, l; r != bytes.length;) {
                                    l = is.read(bytes, r, bytes.length - r);
                                    if (l == -1) {
                                        s.close();
                                        continue c;
                                    }
                                    r += l;
                                }
                                long id = ByteUtils.bytesToLong(bytes);
                                synchronized (map) {
                                    map.put(id, s);
                                    map.notifyAll();
                                }

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }

                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates and returns a DataSocket ID.
     * @return The next data socket ID
     */
    public static long getNextId() {
        return r.nextLong();
    }

    /**
     * Accepts a DataSocket transfer given its ID
     * @param id The id of the DataTransfer socket
     * @return The Socket representing the data transfer with ID id.
     */
    public static Socket accept(long id) {
        while (!map.containsKey(id)) {
            try {
                synchronized (map) {
                    map.wait();
                }
            } catch (Exception e) {
            }
        }
        return map.remove(id);
    }
}
