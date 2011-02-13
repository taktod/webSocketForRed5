package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * WebSocketManager
 * <pre>
 * manage websocket packet.
 * </pre>
 * @author todatakahiko
 */
public class WebSocketManager {
	private WebSocketConnection conn;

	/**
	 * @return the conn
	 */
	public WebSocketConnection getConnection() {
		return conn;
	}
	/**
	 * constructor with session
	 * @param session session instance
	 */
	public WebSocketManager(IoSession session) {
		conn = new WebSocketConnection(session);
	}
	/**
	 * set message for websocket
	 * @param buffer
	 */
	public void setMessage(IoBuffer buffer) {
		if(conn.isConnected()) {
			conn.receive(buffer);
		}
		else {
			// do handshake...
			WebSocketHandshake handshake = new WebSocketHandshake(conn);
			handshake.handShake(buffer);
		}
	}
}
