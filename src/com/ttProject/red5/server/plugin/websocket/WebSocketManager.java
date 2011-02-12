package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public class WebSocketManager {
	private WebSocketConnection conn;

	/**
	 * @return the conn
	 */
	public WebSocketConnection getConn() {
		return conn;
	}
	public WebSocketManager(IoSession session) {
		conn = new WebSocketConnection(session);
	}
	public void setMessage(IoBuffer buffer) {
		System.out.println(buffer.getHexDump());
		if(conn.isConnected()) {
			try {
				System.out.println(getData(buffer));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			// do handshake...
			WebSocketHandshake handshake = new WebSocketHandshake(conn);
			handshake.handShake(buffer);
		}
	}
	private String getData(IoBuffer buffer) throws Exception {
		byte[] b = new byte[buffer.capacity()];
		int i = 0;
		for(byte bi:buffer.array()) {
			i ++;
			if(i == 1) {
				if(bi == 0x00) {
					continue;
				}
				else {
					throw new Exception("first byte must be 0x00 for websocket");
				}
			}
			if(bi == (byte)0xFF) {
				break;
			}
			b[i - 2] = bi;
		}
		return new String(b).trim();
	}
}
