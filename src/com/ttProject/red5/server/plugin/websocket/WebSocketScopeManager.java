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
	public static boolean isPluginedApplication(String application) {
		return pluginedApplicationSet.contains(application);
	}
	/**
	 * @param application application name.
	 */
	public static void addPluginedApplication(String application) {
		pluginedApplicationSet.add(application);
	}
	public void setScope(WebSocketScope scope) {
		scopes.put(scope.getPath(), scope);
	}
}
