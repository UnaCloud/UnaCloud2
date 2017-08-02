package uniandes.unacloud.file.net.torrent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import uniandes.unacloud.common.utils.FileConverter;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

/**
 * Responsible to manage torrent tracker service
 * announce and stop torrent tracking for virtual machines
 * @author CesarF
 *
 */
public class TorrentTracker {
	
	/**
	 * Tracker instance
	 */
	private Tracker tracker;
	
	/**
	 * singleton instance
	 */
	private static TorrentTracker instance;
	
	/**
	 * Tracking url for clients
	 */
	private String tracker_url;
	
	/**
	 * IP address to announce tracker
	 */
	private String IPAddress;
	
	/**
	 * Size of each chunk to tracking
	 */
	private final int pieceLength = 512 * 1024;
	
	/**
	 * Valid ports for clients
	 */
	private int[] listPorts;
	
	/**
	 * List of shared torrents
	 */
	private HashMap<String, Client> localClients;	
	
	/**
	 * Return singleton instance
	 * @return torrent tracker
	 */
	public static TorrentTracker getInstance() {
		if (instance == null) instance = new TorrentTracker();
		return instance;
	}
	
	/**
	 * Constructor
	 */
	private TorrentTracker () {
		localClients = new HashMap<String, Client>();
	}
	
	/**
	 * Init tracking service
	 * @param port
	 * @param ip
	 * @param sourcePath
	 * @throws Exception
	 */
	public void startService(int port, String ip, String sourcePath, int[] clientPorts) throws Exception {
		if (tracker != null) return;
		System.out.println("Configure tracker");
		tracker_url = "http://" + ip + ":" + port;
		IPAddress = ip;
		tracker = new Tracker(new InetSocketAddress(port));
		tracker.start();
		System.out.println("Start tracker " + tracker_url);
		
		listPorts = clientPorts;
		
		System.out.println("load from: " + sourcePath);
		List<File> torrentList = new ArrayList<File>();
		torrentList = getTorrentFiles(torrentList, new File(sourcePath));
		for (File file: torrentList) {
			System.out.println("Load " + file);
			shareTorrent(file);
		}
	}
	
	/**
	 * Return all torrent files in a specified folder
	 * @param listFiles
	 * @param file
	 * @return list of torrent files
	 */
	private List<File> getTorrentFiles(List<File> listFiles, File file) {
		for (File f : file.listFiles())
			if (f.isDirectory())
				getTorrentFiles(listFiles, f);
			else if(f.getName().endsWith(FileConverter.TORRENT_EXTENSION)) {
				listFiles.add(f);
				System.out.println("Found: " + f);
			}
		return listFiles;
	}
	
	/**
	 * Announce a new torrent to agents. Validates if torrent file exits, otherwise creates a new torrent file
	 * @param file
	 * @throws Exception
	 */
	public void publishFile(FileConverter file) throws Exception {

		//---------------------------------------------------------------------
		System.out.println("Parent Directory: " + file);
		//---------------------------------------------------------------------
		
		if (!file.getTorrentFile().exists())
			createTorrent(file.getTorrentFile(), file.getExecutableFile(), tracker_url);
		shareTorrent(file.getTorrentFile());		
	}
	
	/**
	 * Announce a torrent file in tracker
	 * @param torrentFile
	 * @throws Exception
	 */
	private void shareTorrent (File torrentFile) throws Exception {
		
		if (torrentFile == null || !torrentFile.exists()) 
			throw new IllegalArgumentException("Torrent file is not valid");
		if (localClients.containsKey(torrentFile.getAbsolutePath()))
			throw new IllegalArgumentException("Torrent is already announced");
		
		InetAddress ip = InetAddress.getByName(IPAddress);
		Client client = new Client(ip, SharedTorrent.fromFile(torrentFile, torrentFile.getParentFile()), listPorts);
		System.out.println("Loading torrent from " + torrentFile.getName());
		tracker.announce(TrackedTorrent.load(torrentFile));
		 
		System.out.printf("Inet Address: " + ip + " File: " + torrentFile + " Shared: " + torrentFile.getParent());
		client.share();
		localClients.put(torrentFile.getAbsolutePath(), client);
	}
	
	/**
	 * Remove a torrent from the list of shared torrents
	 * @param torrentFile
	 * @throws Exception
	 */
	public void removeTorrent(File torrentFile) throws Exception {
		Client client = localClients.get(torrentFile.getAbsolutePath());
		if (client != null) {
			tracker.remove(client.getTorrent(), 1000);
			client.stop();
			localClients.remove(torrentFile.getAbsolutePath());
		}		
	}
	
	/**
	 * Creates a new torrent file
	 * @param file
	 * @param sharedFile
	 * @param announceURL
	 * @throws IOException
	 */
	public void createTorrent(File file, File sharedFile, String announceURL) throws IOException {
		
		Map<String,Object> info = new HashMap<String,Object>();
		info.put("name", sharedFile.getName());
		info.put("length", sharedFile.length());
		info.put("piece length", pieceLength);
		info.put("pieces", hashPieces(sharedFile, pieceLength));
		Map<String,Object> metainfo = new HashMap<String,Object>();
		metainfo.put("announce", announceURL);
		metainfo.put("info", info);
		OutputStream out = new FileOutputStream(file);
		encodeMap(metainfo, out);
		out.close();
	}
	
	/**
	 * Returns the list of pieces to send
	 * @param file
	 * @param pieceLength
	 * @return
	 * @throws IOException
	 */
	private byte[] hashPieces(File file, int pieceLength) throws IOException {

		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			throw new Error("SHA1 not supported");
		}

		InputStream in = new FileInputStream(file);

		ByteArrayOutputStream pieces = new ByteArrayOutputStream();
		byte[] bytes = new byte[pieceLength];
		int pieceByteCount  = 0, readCount = in.read(bytes, 0, pieceLength);
		while (readCount != -1) {
			pieceByteCount += readCount;
			sha1.update(bytes, 0, readCount);
			if (pieceByteCount == pieceLength) {
				pieceByteCount = 0;
				pieces.write(sha1.digest());
			}
			readCount = in.read(bytes, 0, pieceLength-pieceByteCount);
		}
		in.close();
		if (pieceByteCount > 0)
			pieces.write(sha1.digest());
		return pieces.toByteArray();
	}
	
	/**
	 * Utility method to create torrent file
	 * @param map
	 * @param out
	 * @throws IOException
	 */
	private void encodeMap(Map<String,Object> map, OutputStream out) throws IOException {
		// Sort the map. A generic encoder should sort by key bytes
		SortedMap<String,Object> sortedMap = new TreeMap<String, Object>(map);
		out.write('d');
		for (java.util.Map.Entry<String, Object> e : sortedMap.entrySet()) {
			encodeString(((java.util.Map.Entry<String, Object>) e).getKey(), out);
			encodeObject(((java.util.Map.Entry<String, Object>) e).getValue(), out);
		}
		out.write('e');
	}
	
	/**
	 * Utility method to create torrent file
	 * @param str
	 * @param out
	 * @throws IOException
	 */
	private void encodeString(String str, OutputStream out) throws IOException {
		encodeBytes(str.getBytes("UTF-8"), out);
	}

	/**
	 * Utility method to create torrent file
	 * @param o
	 * @param out
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void encodeObject(Object o, OutputStream out) throws IOException {
		if (o instanceof String)
			encodeString((String)o, out);
		else if (o instanceof Map)
			encodeMap((Map)o, out);
		else if (o instanceof byte[])
			encodeBytes((byte[])o, out);
		else if (o instanceof Number)
			encodeLong(((Number) o).longValue(), out);
		else
			throw new Error("Unencodable type");
	}
	
	/**
	 * Utility method to create torrent file
	 * @param value
	 * @param out
	 * @throws IOException
	 */
	private void encodeLong(long value, OutputStream out) throws IOException {
		out.write('i');
		out.write(Long.toString(value).getBytes("US-ASCII"));
		out.write('e');
	}
	
	/**
	 * Utility method to create torrent file
	 * @param bytes
	 * @param out
	 * @throws IOException
	 */
	private void encodeBytes(byte[] bytes, OutputStream out) throws IOException {
		out.write(Integer.toString(bytes.length).getBytes("US-ASCII"));
		out.write(':');
		out.write(bytes);
	}
}
