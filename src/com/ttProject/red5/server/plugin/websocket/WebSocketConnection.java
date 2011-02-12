package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.session.IoSession;

public class WebSocketConnection {
	private boolean connected = false;
	private IoSession session;
	private String key1;
	private String key2;
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
		key1 = null;
		key2 = null;
	}
	/**
	 * @return the key1
	 */
	public String getKey1() {
		return key1;
	}
	/**
	 * @param key1 the key1 to set
	 */
	public void setKey1(String key1) {
		this.key1 = key1;
	}
	/**
	 * @return the key2
	 */
	public String getKey2() {
		return key2;
	}
	/**
	 * @param key2 the key2 to set
	 */
	public void setKey2(String key2) {
		this.key2 = key2;
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
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
}