package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;

public interface IWebSocketDataListener {
	public void getData(IoBuffer buf);
	public void getMessage(String message);
}
