package com.ttProject.red5.server.plugin.websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSocketScopeManager {
	private static Set<String> pluginedApplicationSet = new HashSet<String>();
	private static Map<String, WebSocketScope> scopes = new HashMap<String, WebSocketScope>();

	/**
	 * @return true:valid application name,
	 */
	public boolean isPluginedApplication(String application) {
		return pluginedApplicationSet.contains(application);
	}
	/**
	 * @param application application name.
	 */
	public void addPluginedApplication(String application) {
		pluginedApplicationSet.add(application);
	}
	/**
	 * @param path scope path.
	 * @return scope instance.
	 */
	public WebSocketScope getScope(String path) {
		return scopes.get(path);
	}
	public void addConnection(WebSocketConnection conn) {
		// コネクションに対するスコープが存在するか確認し、なければ作成する。
		WebSocketScope scope;
		scope = getScope(conn);
		scope.addConnection(conn);
	}
	public void removeConnection(WebSocketConnection conn) {
		WebSocketScope scope;
		scope = getScope(conn);
		scope.removeConnection(conn);
		if(!scope.isValid()) {
			// scope is not valid. delete this.
			scopes.remove(scope);
		}
	}
	public void addListener(IWebSocketDataListener listener) {
		WebSocketScope scope;
		scope = getScope(listener);
		scope.addListener(listener);
	}
	public void removeListener(IWebSocketDataListener listener) {
		WebSocketScope scope;
		scope = getScope(listener);
		scope.removeListener(listener);
		if(!scope.isValid()) {
			// scope is not valid. delete this.
			scopes.remove(scope);
		}
	}
	/**
	 * check the map of scope and get the collect scope.
	 * @param conn 
	 * @return
	 */
	private WebSocketScope getScope(WebSocketConnection conn) {
		WebSocketScope scope;
		if(!scopes.containsKey(conn.getPath())) {
			scope = new WebSocketScope(conn.getPath());
			scopes.put(conn.getPath(), scope);
		}
		else {
			scope = scopes.get(conn.getPath());
		}
		return scope;
	}
	private WebSocketScope getScope(IWebSocketDataListener listener) {
		WebSocketScope scope;
		if(!scopes.containsKey(listener.getPath())) {
			scope = new WebSocketScope(listener.getPath());
			scopes.put(listener.getPath(), scope);
		}
		else {
			scope = scopes.get(listener.getPath());
		}
		return scope;
	}
}
