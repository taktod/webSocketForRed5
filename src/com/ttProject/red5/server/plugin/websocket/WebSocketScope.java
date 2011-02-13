package com.ttProject.red5.server.plugin.websocket;

import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;

public class WebSocketScope {
	private String path;
	private Set<WebSocketConnection> conns = new HashSet<WebSocketConnection>();
	private Set<IWebSocketDataListener> listeners = new HashSet<IWebSocketDataListener>();
	public WebSocketScope(String path) {
		this.path = path; // /room/nameの形でくる。
	}
	// 起動時にわかるはずだから、scopeに登録されていない。applicationに対しては応答しないようにしておく。
	public String getPath() {
		return path;
	}
	public void addConnection(WebSocketConnection conn) {
		conns.add(conn);
	}
	public void removeConnection(WebSocketConnection conn) {
		conns.remove(conn);
		// コネクションがおちたときに、いままでscopeに設置していたconnを消す必要がある。
	}
	public void addListener(IWebSocketDataListener listener) {
		listeners.add(listener);
	}
	public void removeListener(IWebSocketDataListener listener) {
		listeners.remove(listener);
	}
	public boolean isValid() {
		return (conns.size() + listeners.size()) > 0;
	}
	/**
	 * Socket経由できたメッセージに対処する。
	 */
	public void setMessage(IoBuffer buffer) {
		for(IWebSocketDataListener listener:listeners) {
			try {
				listener.getData(buffer);
				listener.getMessage(getData(buffer));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * cut off first 0x00 and last 0xFF
	 * @param buffer input buffer data
	 * @return String data from client
	 * @throws Exception when we get invalid input.
	 */
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
