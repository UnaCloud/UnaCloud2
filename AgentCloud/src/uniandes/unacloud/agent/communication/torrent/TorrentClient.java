package uniandes.unacloud.agent.communication.torrent;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

public class TorrentClient {
	
	public void downloadTorrent(final String torrentPath, String path) {
		
		Client client;
		//Establecer nombre del archivo a crear 
		System.out.println("Start downloading: " + torrentPath);
		//Crea el archivo torrent objetivo
		//		createTorrent(new File(""+file), new File(""+sharedFile), TRACKER_URL);

		try { 

			//Transforma la IP de la maquina de String a InetAddress
			InetAddress ip = InetAddress.getLocalHost();

			System.out.printf("Inet Address: "+ip+"\n"+"File: " + torrentPath+ "\n"+ "Shared: "+path);

			//Metodo del cliente se encarga de obtener el archivo original completo de un Torrent
			client = new Client(ip, 
					SharedTorrent.fromFile(new File(""+torrentPath),new File(""+path)));

			// Establecer limites de subida y bajada de informacion en KB/seg
			client.setMaxDownloadRate(500000.0);
			client.setMaxUploadRate(500000.0);

			//Descarga un archivo de algun servidor

			// Metodo para compartir los torrent locales del cliente, tasa medida en segundos. 
			client.share();
			// Metodo que se encarga de esperar a que el proceso de descarga finalice exitosamente 
			//client.waitForCompletion();
			System.out.println("Start :"+new Date());
			
			client.addObserver(new Observer() {				
				@Override
				public void update(Observable o, Object arg) {
					 Client client = (Client) o;
					 float progress = client.getTorrent().getCompletion();
					 System.out.println("Vamos: "+torrentPath+" --> "+progress);
				}
			});
			client.waitForCompletion();

		} 
		catch (Exception e) {
			System.out.println("NO se creo!");
			e.printStackTrace();
		}
	}

}
