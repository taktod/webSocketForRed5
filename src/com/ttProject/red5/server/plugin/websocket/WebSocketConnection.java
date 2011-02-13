package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * WebSocketConnection
 * <pre>
 * data for connection.
 * </pre>
 */
public class WebSocketConnection {
	private boolean connected = false;
	private IoSession session;
	private String host;
	private String path;
	private String origin;
	/**
	 * constructor
	 */
	public WebSocketConnection(IoSession session) {
		connected = false;
		this.session = session;
	}
	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}
	/**
	 * on connected, put flg and clear keys.
	 */
	public void setConnected() {
		connected = true;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return "ws://" + host + path;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}
	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	/**
	 * @return the session
	 */
	public IoSession getSession() {
		return session;
	}
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		if(path.charAt(path.length()-1) == '/') {
			this.path = path.substring(0, path.length()-1);
		}
		else {
			this.path = path;
		}
	}
	/**
	 * get the connection id
	 * @return id
	 * @throws Exception when the session is invalid...
	 */
	public long getId() throws Exception{
		if(session == null) {
			throw new Exception("invalid connection");
		}
		return session.getId();
	}
	/**
	 * sendmessage to client
	 * @param buffer IoBuffer data
	 */
	public void send(IoBuffer buffer) {
		session.write(buffer);
	}
	/**
	 * sendmessage to client
	 * @param data string data
	 */
	public void send(String data) {
		// 前後に0x00と0xFFをくっつけて、データをおくる。
		IoBuffer buffer = IoBuffer.allocate(data.length() + 4);
		buffer.put((byte)0x00);
		buffer.put(data.getBytes());
		buffer.put((byte)0xFF);
		buffer.flip();
		session.write(buffer);
	}
	/**
	 * close Connection
	 */
	public void close() {
		WebSocketScopeManager manager = new WebSocketScopeManager();
		manager.removeConnection(this);
		session.close(true);
	}
}
