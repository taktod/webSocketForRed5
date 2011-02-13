package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Listener for execute packet data.
 */
public interface IWebSocketDataListener {
	/**
	 * @return path of the scope
	 */
	public String getPath();
	/**
	 * execute byte data.
	 * @param buf
	 */
	public void getData(IoBuffer buf);
	/**
	 * execute string data.
	 * <pre>
	 * pre-convertted into string(SJIS).
	 * </pre>
	 * @param message
	 */
	public void getMessage(String message);
}
