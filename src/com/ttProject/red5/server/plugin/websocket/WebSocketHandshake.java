package com.ttProject.red5.server.plugin.websocket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.mina.core.buffer.IoBuffer;

public class WebSocketHandshake {
	private WebSocketConnection conn;
	public WebSocketHandshake(WebSocketConnection conn) {
		this.conn = conn;
	}
	public void handShake(IoBuffer buffer) {
		byte[] b = new byte[buffer.capacity()];
		String data;
		int i = 0;
		for(byte bi:buffer.array()) {
			if(bi == 0x0D || bi == 0x0A) {
				if(b.length != 0) {
					data = (new String(b)).trim();
					if(data.contains("GET ")) {
						String[] ary = data.split("GET ");
						ary = ary[1].split(" HTTP/1.1");
						conn.setPath(ary[0]);
					}
					else if(data.contains("Sec-WebSocket-Key1")) {
						conn.setKey1(data);
					}
					else if(data.contains("Sec-WebSocket-Key2")) {
						conn.setKey2(data);
					}
					else if(data.contains("Host")) {
						String[] ary = data.split("Host: ");
						conn.setHost(ary[1]);
					}
					else if(data.contains("Origin")) {
						String[] ary = data.split("Origin: ");
						conn.setOrigin(ary[1]);
					}
					if(data.length() > 4) {
						System.out.println(data);
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
		doHandShake(b);
	}
	private void doHandShake(byte[] key3) {
		if(key3 == null) {
			System.out.println("last byte is incollect");
			return;
		}
		String key1 = conn.getKey1();
		String key2 = conn.getKey2();
		if(key1 == null || key2 == null) {
			System.out.println("key data is missing");
			return;
		}
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
			result = crypt(b);
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return;
		}
		IoBuffer buf = IoBuffer.allocate(2048);
		byte[] bb = {0x0D, 0x0A};
		buf.put("HTTP/1.1 101 WebSocket Protocol Handshake".getBytes());
		buf.put(bb);
		buf.put("Upgrade: WebSocket".getBytes());
		buf.put(bb);
		buf.put(("Sec-WebSocket-Origin: " + conn.getOrigin()).getBytes());
		buf.put(bb);
		buf.put(("Sec-WebSocket-Location: " + conn.getHost()).getBytes());
		buf.put(bb);
		buf.put("Sec-WebSocket-Protocol: sample".getBytes());
		buf.put(bb);
		buf.put(bb);
		buf.put(result);
		buf.flip();
		conn.getSession().write(buf);
		conn.setConnected();
		System.out.println("HandShake complete");
	}
	private Integer getKeyInteger(String key) {
		StringBuffer numList = new StringBuffer();
		int spaceCount = 0;
		for(int i=20;i < key.length(); i++) {
			char c = key.charAt(i);
			if(c >= 0x30 && c < 0x3A) {
				// ”Žš‚Ì€‚È‚Ì‚Å•¶Žš—ñ‚É‰Á‚¦‚éB
				numList.append(c);
			}
			else if(c == ' ') {
				spaceCount ++;
			}
		}
		return (int)(new Long(numList.toString()) / spaceCount);
	}
	private byte[] crypt(byte[] bytes) throws NoSuchAlgorithmException {
		if(bytes == null || bytes.length == 0) {
			throw new IllegalArgumentException("bytes for encrypt must have body");
		}
		MessageDigest md = MessageDigest.getInstance("MD5");
		return md.digest(bytes);
	}
}
