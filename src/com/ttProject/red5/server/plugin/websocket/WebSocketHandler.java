package com.ttProject.red5.server.plugin.websocket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocketHandler
 * <pre>
 * IoHandlerAdapter for webSocket
 * </pre>
 * @author todatakahiko
 */
public class WebSocketHandler extends IoHandlerAdapter{
	private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {		
		log.error("exception", cause);
		super.exceptionCaught(session, cause);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		session.setAttribute("manager", new WebSocketManager(session));
		super.sessionCreated(session);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// connectionをスコープから削除する。
		WebSocketManager manager = (WebSocketManager)session.getAttribute("manager");
		manager.getConnection().close();
		super.sessionClosed(session);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if(message instanceof IoBuffer) {
			WebSocketManager manager = (WebSocketManager)session.getAttribute("manager");
			manager.setMessage((IoBuffer)message);
		}
		super.messageReceived(session, message);
	}
}
