package dataChannel;

import com.losandes.utils.ByteUtils;
import com.losandes.utils.VariableManager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class responsible for conecting to a given service on a host and requesting a data transfer socket
 * @author Clouder
 */
public class DataSocketConnection {

    /**
     * Creates a new conection to the host with the IP address <i>ip</i> and request a data server socket using the ID <i>id</i>
     * @param ip Ip address of the host to conecto to
     * @param id Id of the data socket to be used
     * @return A Socket representing the data socket with the given id
     * @throws UnknownHostException If there is an error connecting to the given host
     * @throws IOException If there is an error on data socket request
     */
    public static Socket connect(String ip,long id) throws UnknownHostException, IOException{
        Socket s = new Socket(ip,VariableManager.global.getIntValue("DATA_SOCKET"));
        s.getOutputStream().write(ByteUtils.longToBytes(id));
        return s;
    }



}
