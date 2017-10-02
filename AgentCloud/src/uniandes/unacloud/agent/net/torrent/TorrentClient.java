package uniandes.unacloud.agent.net.torrent;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

/**
 * Class responsible to manage torrents 
 * @author CesarF
 *
 */
public class TorrentClient {
	
	/**
	 * Torrent client instance
	 */
	private static TorrentClient instance;
	
	/**
	 * List of current clients announcing torrents
	 */
	private HashMap<String, Client> localClients;	
	
	/**
	 * List of valid ports to share chunks
	 */
	private int[] listPorts;
	
	/**
	 * If service is running 
	 */	
	private boolean running;
	
	/**
	 * Return singleton instance
	 * @return
	 */
	public static TorrentClient getInstance() {
		if (instance == null) instance = new TorrentClient();
		return instance;
	}
	
	/**
	 * Constructor 
	 */
	private TorrentClient() {
		localClients = new HashMap<String, Client>();
		running = false;
	}
	
	/**
	 * Start torrent client service
	 * @param ports
	 */
	public void startService(int[] ports) {
		if (running) return;
		listPorts = ports;
		running = true;
	}
	
	/**
	 * Download and announce file based in torrent file
	 * @param torrent
	 * @throws Exception
	 */
	public void downloadAndAnnounceTorrent(File torrent) throws Exception {
		announceTorrent(torrent, true);
	}
	
	/**
	 * Announce a torrent file if this is not current announced
	 * @param torrent
	 * @throws Exception
	 */
	public void announceTorrent(File torrent) throws Exception {
		announceTorrent(torrent, false);
	}
	
	/**
	 * Download file if it is necessary, and announce torrent 
	 * @param torrentFile
	 * @param download
	 * @throws Exception
	 */
	private void announceTorrent(File torrentFile, boolean download) throws Exception {
		if (torrentFile == null || !torrentFile.exists()) 
			throw new IllegalArgumentException("Torrent file is not valid");
		if (localClients.containsKey(torrentFile.getAbsolutePath()))
			throw new IllegalArgumentException("Torrent is already announced");
		
		System.out.println("Torrent: " + torrentFile);
		InetAddress ip = InetAddress.getLocalHost();
		System.out.println("\tInet Address: " + ip);
		System.out.println("\t\t File: " + torrentFile);
		Client client = new Client(ip, SharedTorrent.fromFile(torrentFile, torrentFile.getParentFile()), listPorts);
		System.out.println("\t" + client);
		client.setMaxDownloadRate(500000.0);
		client.setMaxUploadRate(500000.0);
		if (download)
			downloadTorrent(client);
		client.share();
		System.out.println("Shared");
		localClients.put(torrentFile.getAbsolutePath(), client);
	}
	
	/**
	 * Download file base in torrent file
	 * @param client
	 * @throws Exception
	 */
	private void downloadTorrent(Client client) throws Exception {

		client.download();			
		
		client.addObserver(new Observer() {				
			@Override
			public void update(Observable o, Object arg) {
				 Client client = (Client) o;
				 float progress = client.getTorrent().getCompletion();
				 System.out.println("**** Process torrent: " + client.getTorrent().getName() + " --> " + progress);
			}
		});
		
		client.waitForCompletion();
		System.out.println("Downloaded");
	}
	
	/**
	 * Remove a torrent from the list of shared torrents
	 * @param torrentFile
	 * @throws Exception
	 */
	public void removeTorrent(File torrentFile) throws Exception {
		Client client = localClients.get(torrentFile.getAbsolutePath());
		System.out.println("Remove torrent " + torrentFile.getAbsolutePath() + " client: " + client);
		if (client != null) {
			try {
				client.getTorrent().stop();
				client.getTorrent().close();
				if (client.getTorrent().isComplete())
					client.getTorrent().finish();
				client.stop(false);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			localClients.remove(torrentFile.getAbsolutePath());
		}		
	}
}
