package com.ttProject.red5.server.plugin.websocket;

public class WebSocketPlugin {
	private String setting;
	public WebSocketPlugin() {
		System.out.println("constructor");
	}
	public void setSetting(String setting) {
		this.setting = setting;
	}
	public void doStart() {
		System.out.println("doStart" + setting);
	}
}
