package com.ttProject.red5.server.plugin.websocket;

import java.util.HashMap;
import java.util.Map;

/**
 * to manage scopes.
 * @author todatakahiko
 */
public class WebSocketScopeManager {
	private static Map<String, IWebSocketDataListener> pluginedApplicationMap = new HashMap<String, IWebSocketDataListener>();
	private static Map<String, WebSocketScope> scopes = new HashMap<String, WebSocketScope>();

	/**
	 * @return true:valid application name,
	 */
	public boolean isPluginedApplication(String scope) {
		// figure out application name from access path.
		String[] data = scope.split("/", 2);
		return pluginedApplicationMap.containsKey(data[0]);
	}
	/**
	 * @param application application name.
	 */
	public void addPluginedApplication(String application, IWebSocketDataListener listener) {
		pluginedApplicationMap.put(application, listener);
	}
	/**
	 * @param path scope path.
	 * @return scope instance.
	 */
	public WebSocketScope getScope(String path) {
		return scopes.get(path);
	}
	/**
	 * add the connection on scope.
	 * @param conn WebSocketConnection
	 */
	public void addConnection(WebSocketConnection conn) {
		WebSocketScope scope;
		scope = getScope(conn);
		scope.addConnection(conn);
	}
	/**
	 * remove connection from scope.
	 * @param conn WebSocketConnection
	 */
	public void removeConnection(WebSocketConnection conn) {
		WebSocketScope scope;
		scope = getScope(conn);
		scope.removeConnection(conn);
		if(!scope.isValid()) {
			// scope is not valid. delete this.
			scopes.remove(scope);
		}
	}
	/**
	 * get corresponging scope, if no scope, make new one.
	 * @param conn 
	 * @return
	 */
	private WebSocketScope getScope(WebSocketConnection conn) {
		if(!scopes.containsKey(conn.getPath())) {
			WebSocketScope scope;
			scope = new WebSocketScope(conn.getPath(), getListenerFromApplication(conn.getPath()));
			scopes.put(conn.getPath(), scope);
			return scope;
		}
		else {
			return scopes.get(conn.getPath());
		}
	}
	/**
	 * pathからListenerオブジェクトを取得し、登録する。
	 * @param path
	 * @return
	 */
	private IWebSocketDataListener getListenerFromApplication(String path) {
		String[] data = path.split("/", 2);
		return pluginedApplicationMap.get(data[0]);
	}
}
