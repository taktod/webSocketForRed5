package com.ttProject.red5.server.plugin.websocket;

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
 *
 */
public class WebSocketHandler extends IoHandlerAdapter{
	private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {		
		log.error("exception", cause);
		super.exceptionCaught(session, cause);
	}
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		super.messageReceived(session, message);
	}
}
