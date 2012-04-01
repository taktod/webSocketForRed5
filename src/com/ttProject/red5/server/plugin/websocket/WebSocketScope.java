package com.ttProject.red5.server.plugin.websocket;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * スコープでリスナーを選択する部分を変更して、アプリケーション(スコープの頭の部分)で振り分けを実行して動作するようにしておく。
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
		this.path = path; // /room/name
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
//		for(IWebSocketDataListener listener:listeners) {
//			listener.connect(conn);
//		}
	}
	/**
	 * remove connection from scope
	 * @param conn WebSocketConnection
	 */
	public void removeConnection(WebSocketConnection conn) {
		conns.remove(conn);
		listener.leave(conn);
//		for(IWebSocketDataListener listener:listeners) {
//			listener.leave(conn);
//		}
	}
	/**
	 * add new listener on scope
	 * @param listener IWebSocketDataListener
	 * /
	public void addListener(IWebSocketDataListener listener) {
		listeners.add(listener);
	}
	/**
	 * remove listener from scope
	 * @param listener IWebSocketDataListener
	 * /
	public void removeListener(IWebSocketDataListener listener) {
		System.out.println("remove:" + listener.getPath());
//		listeners.remove(listener);
	}
	/**
	 * check the scope state.
	 * @return true:still have relation
	 */
	public boolean isValid() {
		return conns.size() > 0;
//		return (conns.size() + listeners.size()) > 0;
	}
	/**
	 * get the message from client
	 */
	public void receiveData(WebSocketConnection conn, Object buffer) {
		listener.receiveData(conn, buffer);
	}
	/**
	 * cut off first 0x00 and last 0xFF
	 * @param buffer input buffer data
	 * @return String data from client
	 * @throws UnsupportedEncodingException 
	 * @throws WebSocketException when we get invalid input.
	 */
	@SuppressWarnings("unused")
	private String getData(IoBuffer buffer) throws WebSocketException, UnsupportedEncodingException {
		byte[] b = new byte[buffer.capacity()];
		int i = 0;
		for(byte bi:buffer.array()) {
			i ++;
			if(i == 1) {
				if(bi == 0x00) {
					continue;
				}
				else {
					throw new WebSocketException("first byte must be 0x00 for websocket");
				}
			}
			if(bi == (byte)0xFF) {
				break;
			}
			b[i - 2] = bi;
		}
		// TODO Handle all data as UTF-8
		return new String(b, "SJIS").trim();
	}
}
