package com.ttProject.red5.server.plugin.websocket;

import java.util.Set;

public class WebSocketScope {
	private String application;
	private String path;
	private Set<WebSocketConnection> conns;
	public WebSocketScope(String application, String path) {
		this.application = application;
		this.path = path; // /room/nameの形でくる。
	}
	// 起動時にわかるはずだから、scopeに登録されていない。applicationに対しては応答しないようにしておく。
	public String getPath() {
		return application + path;
	}
	public void addConnection(WebSocketConnection conn) {
		conns.add(conn);
	}
	public void removeConnection(WebSocketConnection conn) {
		conns.remove(conn);
		// コネクションがおちたときに、いままでscopeに設置していたconnを消す必要がある。
	}
}
