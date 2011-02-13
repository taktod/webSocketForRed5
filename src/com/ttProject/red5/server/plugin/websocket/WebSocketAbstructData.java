package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.api.IScope;

public abstract class WebSocketAbstructData implements IWebSocketDataListener {
	protected String path;
	public WebSocketAbstructData(IScope scope) {
		String path = scope.getPath() + "/" + scope.getName();
		System.out.println("WebSocketAbstructData:" + path);
		String[] ary = path.split("/default");
		this.path = ary[1];
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
