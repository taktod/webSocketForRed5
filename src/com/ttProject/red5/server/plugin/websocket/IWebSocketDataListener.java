package com.ttProject.red5.server.plugin.websocket;

/**
 * Listener for execute packet data.
 */
public interface IWebSocketDataListener {
	/**
	 * execute byte data.
	 * @param buf
	 */
	public void receiveData(WebSocketConnection conn, Object data);
	/**
	 * on connect new WebSocket client
	 * @param conn WebSocketConnection
	 */
	public void connect(WebSocketConnection conn);
	/**
	 * on leave WebSocket client
	 * @param conn WebSocketConnection
	 */
	public void leave(WebSocketConnection conn);
}
