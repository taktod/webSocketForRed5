package com.ttProject.red5.server.plugin.websocket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.Base64;

/**
 * WebSocketHandshake
 * <pre>
 * this class for handshake process.
 * </pre>
 */
public class WebSocketHandshake {
	private WebSocketConnection conn;
	private String key1;
	private String key2;
	private String origin;
	private String host;
	private String path;
	private byte[] key3;
	private String version;
	private String key;
	/**
	 * constructor with connection object.
	 * @param conn connection object.
	 */
	public WebSocketHandshake(WebSocketConnection conn) {
		this.conn = conn;
	}
	/**
	 * handShake
	 * <pre>
	 * analyze handshake input from client.
	 * </pre>
	 * @param buffer ioBuffer
	 */
	public void handShake(IoBuffer buffer) throws WebSocketException {
		byte[] b = new byte[buffer.capacity()];
		String data;
		int i = 0;
		version = "";
		System.out.println("start...");
		for(byte bi:buffer.array()) {
			if(bi == 0x0D || bi == 0x0A) {
				if(b.length != 0) {
					data = (new String(b)).trim();
					if(data.contains("GET ")) {
						// get the path data for handShake
						Pattern pattern = Pattern.compile("GET \\/([\\w\\/]*)\\??(.*) HTTP\\/1.1");
						Matcher matcher = pattern.matcher(data);
						if(matcher.find()) {
							WebSocketScopeManager manager = new WebSocketScopeManager();
							if(matcher.groupCount() != 2 || !manager.isPluginedApplication(matcher.group(1))) {
								// invalid scope or application is not registered. failed for handshake.
								break;
							}
							path = matcher.group(1);
							if(matcher.group(2) != null && !"".equals(matcher.group(2))) {
								path += "?" + matcher.group(2);
							}
							conn.setPath(matcher.group(1));
							conn.setQuery(matcher.group(2));
						}
					}
					else if(data.contains("Sec-WebSocket-Key1: ")) {
						// get the key1 data
						key1 = data;
					}
					else if(data.contains("Sec-WebSocket-Key2: ")) {
						// get the key2 data
						key2 = data;
					}
					else if(data.contains("Host: ")) {
						// get the host data
						String[] ary = data.split("Host: ");
						host = ary[1];
						conn.setHost(ary[1]);
					}
					else if(data.contains("Origin: ")) {
						// get the origin data
						String[] ary = data.split("Origin: ");
						origin = ary[1];
						conn.setOrigin(ary[1]);
					}
					else if(data.contains("Sec-WebSocket-Key: ")) {
						String[] ary = data.split("Sec-WebSocket-Key: ");
						key = ary[1];
					}
					else if(data.contains("Sec-WebSocket-Version: ")) {
						String[] ary = data.split("Sec-WebSocket-Version: ");
						version = ary[1];
						conn.setVersion(version);
					}
				}
				i = 0;
				b = new byte[buffer.capacity()];
			}
			else {
				b[i] = bi;
				i ++;
			}
		}
		key3 = b;
		// start the handshake reply
		if(version.equals("")) {
			try {
//				System.out.println("hybi 00");
				doHybi00HandShake();
			}
			catch(WebSocketException e) {
				System.out.println("exception occuered...");
				e.printStackTrace();
				// send eof before closing connection.
				IoBuffer buf = IoBuffer.allocate(4);
				buf.put(new byte[]{(byte)0xFF, (byte)0x00});
				buf.flip();
				conn.send(buf);
				// close connection.
				conn.close();
				throw e; // throw the data again.
			}
		}
		else {
			try {
//				System.out.println("rfc6455");
				doRFC6455Handshake();
			}
			catch (WebSocketException e) {
				System.out.println("exception occuered...");
				e.printStackTrace();
				// TODO have to understand close for rfc6455
				conn.close();
				throw e;
			}
		}
	}
	private void doRFC6455Handshake() throws WebSocketException {
		if(key == null) {
			throw new WebSocketException("key data is missing!");
		}
		String guid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] dat = (key + guid).getBytes();
			md.update(dat);
			IoBuffer buf = IoBuffer.allocate(2048);
			byte[] bb = {0x0D, 0x0A};
			buf.put("HTTP/1.1 101 Web Socket Protocol Handshake".getBytes());
			buf.put(bb);
			buf.put("Upgrade: WebSocket".getBytes());
			buf.put(bb);
			buf.put("Connection: Upgrade".getBytes());
			buf.put(bb);
			buf.put("Sec-WebSocket-Accept: ".getBytes());
			buf.put((new String(Base64.encodeBase64Chunked(md.digest()))).getBytes());
			buf.put(bb);
			buf.flip();
			// write the data on session
			conn.getSession().write(buf);
			// handshake is finished.
			conn.setConnected();
			WebSocketScopeManager manager = new WebSocketScopeManager();
			manager.addConnection(conn);
			System.out.println("HandShake complete");
		}
		catch (Exception e) {
		}
	}
	/**
	 * start the handshake reply
	 * @param key3
	 */
	private void doHybi00HandShake() throws WebSocketException {
		if(path == null) {
			throw new WebSocketException("get data is invalid!");
		}
		if(key3 == null) {
			throw new WebSocketException("last byte is incollect!");
		}
		if(key1 == null || key2 == null) {
			throw new WebSocketException("key data is missing!");
		}
		// calicurate first 16 byte of integer data;
		byte[] b = new byte[16];
		int buf1 = getKeyInteger(key1);
		int buf2 = getKeyInteger(key2);
		byte[] result;
		try {
			b[0] = (byte)((buf1 & 0xFF000000) >> 24);
			b[1] = (byte)((buf1 & 0x00FF0000) >> 16);
			b[2] = (byte)((buf1 & 0x0000FF00) >> 8);
			b[3] = (byte)((buf1 & 0x000000FF));
			b[4] = (byte)((buf2 & 0xFF000000) >> 24);
			b[5] = (byte)((buf2 & 0x00FF0000) >> 16);
			b[6] = (byte)((buf2 & 0x0000FF00) >> 8);
			b[7] = (byte)((buf2 & 0x000000FF));
			b[8]  = key3[0];
			b[9]  = key3[1];
			b[10] = key3[2];
			b[11] = key3[3];
			b[12] = key3[4];
			b[13] = key3[5];
			b[14] = key3[6];
			b[15] = key3[7];
			// make MD5 byte data.
			result = crypt(b);
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new WebSocketException("MD5 algorithm is missing.");
		}
		catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new WebSocketException("Data is too short.");
		}
		// make up reply data...
		IoBuffer buf = IoBuffer.allocate(2048);
		byte[] bb = {0x0D, 0x0A};
		buf.put("HTTP/1.1 101 WebSocket Protocol Handshake".getBytes());
		buf.put(bb);
		buf.put("Upgrade: WebSocket".getBytes());
		buf.put(bb);
		buf.put("Connection: Upgrade".getBytes());
		buf.put(bb);
		buf.put(("Sec-WebSocket-Origin: " + origin).getBytes());
		buf.put(bb);
		buf.put(("Sec-WebSocket-Location: " + "ws://" + host + "/" + path).getBytes());
		buf.put(bb);
		buf.put("Sec-WebSocket-Protocol: sample".getBytes());
		buf.put(bb);
		buf.put(bb);
		buf.put(result);
		buf.flip();
		// write the data on session
		conn.getSession().write(buf);
		// handshake is finished.
		conn.setConnected();
		// scope register
		WebSocketScopeManager manager = new WebSocketScopeManager();
		manager.addConnection(conn);
		System.out.println("HandShake complete");
	}
	/**
	 * calicurate integer data.
	 * @param key input key string data.
	 * @return integer data after calicurate
	 */
	private Integer getKeyInteger(String key) {
		StringBuffer numList = new StringBuffer();
		int spaceCount = 0;
		for(int i=20;i < key.length(); i++) {
			char c = key.charAt(i);
			if(c >= 0x30 && c < 0x3A) {
				// this is number data.
				numList.append(c);
			}
			else if(c == ' ') {
				// this is space data.
				spaceCount ++;
			}
		}
		return (int)(new Long(numList.toString()) / spaceCount);
	}
	/**
	 * make md5 data.
	 * @param bytes input bytes
	 * @return crypted bytes data
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] crypt(byte[] bytes) throws NoSuchAlgorithmException {
		if(bytes == null || bytes.length == 0) {
			throw new IllegalArgumentException("bytes for encrypt must have body");
		}
		MessageDigest md = MessageDigest.getInstance("MD5");
		return md.digest(bytes);
	}
}
