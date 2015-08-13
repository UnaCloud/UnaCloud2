package fileTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import communication.UnaCloudMessage;
import communication.security.utils.AbstractCommunicator;
import dataChannel.DataServerSocket;

/**
 * Class responsible for attending requests to retrieve the contens of a local folder and send it to UnaCloud server
 * @author Clouder
 */
public class UnicastSender {

    /**
     *
     * @param solicitud UnaCloud server request to retrieve a folder
     * @param conexion The connection to be used to stablish the file retrieval process
     * @throws Exception If there is an error reading the requested folder
     */
    public void attendFileRetrieveRequest(UnaCloudMessage solicitud, AbstractCommunicator conexion) throws Exception {
        String ruta = solicitud.getString(2),longid=solicitud.getString(3);
        long id = Long.parseLong(longid);
        File archivosAEnviar[] = getFolderFiles(ruta);
        String[] respuesta = new String[2 + archivosAEnviar.length * 2];
        respuesta[0] = "" + archivosAEnviar.length;
        for (int e = 0; e < archivosAEnviar.length; e++) {
            respuesta[e * 2 + 1] = archivosAEnviar[e].getName();
            respuesta[e * 2 + 2] = "" + archivosAEnviar[e].length();
        }
        conexion.writeUTF(respuesta);
        Socket s = DataServerSocket.accept(id);
        byte[] buffer = new byte[1024*100];
        OutputStream os=s.getOutputStream();
        for (int e = 0,l; e < archivosAEnviar.length; e++) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(archivosAEnviar[e]);
                while ((l = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, l);
                }
            } catch (IOException ex) {
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                }
            }
        }
        try {
            s.close();
        } catch (IOException ex) {
        }
        conexion.close();
    }

    /**
     * Return a list of readable files to be send by the request attender
     * @param path The folder path that must be examined
     * @return An array containing the children files of the given folder path
     */
    private File[] getFolderFiles(String path) {
        ArrayList<File> archivos = new ArrayList<File>();
        File maquina = new File(path).getParentFile();
        for (File c : maquina.listFiles()) {
            if (c.isFile()) {
                archivos.add(c);
            }
        }
        return archivos.toArray(new File[archivos.size()]);
    }
}
