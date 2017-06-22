package uniandes.unacloud.file.com.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.turn.ttorrent.tracker.Tracker;

public class TorrentServer {
	
	private Tracker tracker;
	
	public static TorrentServer instance;
	
	public static TorrentServer getInstance() throws IOException {
		if (instance == null) instance = new TorrentServer();
		return instance;
	}
	
	private TorrentServer () throws IOException {
		tracker = new Tracker(new InetSocketAddress(10031));
		tracker.start();
	}
	
	

}
