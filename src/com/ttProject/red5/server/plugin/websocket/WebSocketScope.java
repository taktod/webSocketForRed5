package com.ttProject.red5.server.plugin.websocket;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/**
 * group the connection for same path
 * @author taktod
 */
public class WebSocketScope {
	private String path;
	private Set<WebSocketConnection> conns = new HashSet<WebSocketConnection>();
	private IWebSocketDataListener listener;
	/**
	 * constructor
	 * @param path path data
	 */
	public WebSocketScope(String path, IWebSocketDataListener listener) {
		this.path = path; // room/name
		this.listener = listener;
	}
	/**
	 * get the set of connections
	 * @return the conns
	 */
	public Set<WebSocketConnection> getConns() {
		return conns;
	}
	/**
	 * get the path info of scope
	 * @return path data.
	 */
	public String getPath() {
		return path;
	}
	/**
	 * add new connection on�@scope
	 * @param conn WebSocketConnection
	 */
	public void addConnection(WebSocketConnection conn) {
		conns.add(conn);
		listener.connect(conn);
	}
	/**
	 * remove connection from scope
	 * @param conn WebSocketConnection
	 */
	public void removeConnection(WebSocketConnection conn) {
		conns.remove(conn);
		listener.leave(conn);
	}
	/**
	 * check the scope state.
	 * @return true:still have relation
	 */
	public boolean isValid() {
		return conns.size() > 0;
	}
	/**
	 * get the message from client
	 */
	public void receiveData(WebSocketConnection conn, Object buffer) {
		listener.receiveData(conn, buffer);
	}
	/**
	 * 
	 * @param message
	 * @throws UnsupportedEncodingException
	 */
	public void sendAll(String message) throws UnsupportedEncodingException{
		for(WebSocketConnection conn : conns) {
			conn.send(message);
		}
	}
}
