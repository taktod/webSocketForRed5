package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;

public abstract class WebSocketAbstructData implements IWebSocketDataListener {
	private String path;
	public WebSocketAbstructData(WebSocketConnection conn) {
		String path = conn.getPath();
		if(path.charAt(path.length() - 1) == '/') {
			this.path = "/" + path.substring(0, path.length() - 1);
		}
		else {
			this.path = "/" + path;
		}
	}
	@Override
	public String getPath() {
		return path;
	}

	@Override
	public abstract void getData(IoBuffer buf);

	@Override
	public abstract void getMessage(String message);
}
