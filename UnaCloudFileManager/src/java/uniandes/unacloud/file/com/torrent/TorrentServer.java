package uniandes.unacloud.file.com.torrent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

public class TorrentServer {
	
	private Tracker tracker;
	
	public static TorrentServer instance;
	
	private String tracker_url;
	
	private String IPAddress;
	
	public static TorrentServer getInstance() {
		if (instance == null) instance = new TorrentServer();
		return instance;
	}
	
	private TorrentServer () {
		
	}
	
	public void startService(int port, String ip, String sourcePath) throws Exception {
		if (tracker != null) return;
		System.out.println("Configure tracker");
		tracker_url = "http://"+ip+":"+port+"/announce";
		IPAddress = ip;
		tracker = new Tracker(new InetSocketAddress(port));
		tracker.start();
		System.out.println("Start tracker "+tracker_url);
		
		System.out.println("load from: "+sourcePath);
		List<File> torrentList = new ArrayList<File>();
		torrentList = getTorrentFiles(torrentList, new File(sourcePath));
		for (File file: torrentList) {
			System.out.println("Load "+file);
			shareTorrent(file);
		}
	}
	
	private List<File> getTorrentFiles(List<File> listFiles, File file) {
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				getTorrentFiles(listFiles, f);
			}
			else if(f.getName().endsWith(".torrent")) {
				listFiles.add(f);
				System.out.println("Found: "+f);
			}
				
		}
		return listFiles;
	}
	
	public void publishFile(File file) throws Exception {

		//---------------------------------------------------------------------
		System.out.println("Parent Directory: " + file);
		//---------------------------------------------------------------------
		
		File torrentFile = new File(file.getAbsolutePath()+".torrent");
		createTorrent(torrentFile, file, tracker_url);
		shareTorrent(torrentFile);
		
	}
	
	private void shareTorrent (File torrentFile) throws Exception{
		InetAddress ip = InetAddress.getByName(IPAddress);
		Client client = new Client(ip, SharedTorrent.fromFile(torrentFile, torrentFile.getParentFile()));
		
		System.out.println("Loading torrent from " + torrentFile.getName());
		tracker.announce(TrackedTorrent.load(torrentFile));
		 
		System.out.printf("Inet Address: "+ip+" File: " + torrentFile+" Shared: "+torrentFile.getParent());
		client.share();
	}
	
	
	public void createTorrent(File file, File sharedFile, String announceURL) throws IOException {
		final int pieceLength = 512*1024;
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
	
	private void encodeMap(Map<String,Object> map, OutputStream out) throws IOException{
		// Sort the map. A generic encoder should sort by key bytes
		SortedMap<String,Object> sortedMap = new TreeMap<String, Object>(map);
		out.write('d');
		for (java.util.Map.Entry<String, Object> e : sortedMap.entrySet()) {
			encodeString(((java.util.Map.Entry<String, Object>) e).getKey(), out);
			encodeObject(((java.util.Map.Entry<String, Object>) e).getValue(), out);
		}
		out.write('e');
	}
	
	private void encodeString(String str, OutputStream out) throws IOException {
		encodeBytes(str.getBytes("UTF-8"), out);
	}

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
	
	private void encodeLong(long value, OutputStream out) throws IOException {
		out.write('i');
		out.write(Long.toString(value).getBytes("US-ASCII"));
		out.write('e');
	}
	
	private void encodeBytes(byte[] bytes, OutputStream out) throws IOException {
		out.write(Integer.toString(bytes.length).getBytes("US-ASCII"));
		out.write(':');
		out.write(bytes);
	}
}
