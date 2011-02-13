package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.api.IScope;

/**
 * 
 * @author todatakahiko
 */
public abstract class WebSocketAbstructDataListener implements IWebSocketDataListener {
	protected String path;
	/**
	 * constructor with scope input.
	 * <pre>
	 * to make default path.
	 * </pre>
	 */
	public WebSocketAbstructDataListener(IScope scope) {
		String path = scope.getPath() + "/" + scope.getName();
		System.out.println("WebSocketAbstructData:" + path);
		String[] ary = path.split("/default");
		this.path = ary[1];
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void getData(IoBuffer buf);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void getMessage(String message);
}
