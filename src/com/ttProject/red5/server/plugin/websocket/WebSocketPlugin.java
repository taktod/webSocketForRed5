package com.ttProject.red5.server.plugin.websocket;

import org.red5.server.Server;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.plugin.Red5Plugin;

/**
 * WebSocketPlugin
 * <pre>
 * This program will be called by red5 PluginLauncher
 * and hold the application Context or Application Adapter
 * </pre>
 * @author Toda Takahiko
 *
 */
public class WebSocketPlugin extends Red5Plugin{
	public WebSocketPlugin() {
	}
	@Override
	public void doStart() throws Exception {
		// TODO Auto-generated method stub
		super.doStart();
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}
	@Override
	public Server getServer() {
		// TODO Auto-generated method stub
		return super.getServer();
	}
	@Override
	public void setApplication(MultiThreadedApplicationAdapter application) {
		// TODO Auto-generated method stub
		super.setApplication(application);
	}
}
