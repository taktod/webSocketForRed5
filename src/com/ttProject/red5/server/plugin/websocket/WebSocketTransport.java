package com.ttProject.red5.server.plugin.websocket;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocketTransport
 * <pre>
 * this class will be instanced on red5.xml(or other xml files).
 * will make port listen...
 * </pre>
 */
public class WebSocketTransport {
	private static final Logger log = LoggerFactory.getLogger(WebSocketTransport.class);
	private static final int DEFAULT_CONN_THREADS = Runtime.getRuntime().availableProcessors();
	private static final int DEFAULT_IO_THREADS = DEFAULT_CONN_THREADS * 2;
	private static final int DEFAULT_RECEIVE_BUFFER_SIZE = 2048;
	private static final int DEFAULT_SEND_BUFFER_SIZE = 2048;
	private static final int DEFAULT_WEBSOCKET_PORT = 80;
	private int sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
	private int receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
	private int connectionThreads = DEFAULT_CONN_THREADS;
	private int ioThreads = DEFAULT_IO_THREADS;
	private int port = DEFAULT_WEBSOCKET_PORT;
	private IoHandlerAdapter ioHandler;
	private SocketAcceptor acceptor;
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @param sendBufferSize the sendBufferSize to set
	 */
	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}
	/**
	 * @param receiveBufferSize the receiveBufferSize to set
	 */
	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}
	/**
	 * @param connectionThreads the connectionThreads to set
	 */
	public void setConnectionThreads(int connectionThreads) {
		this.connectionThreads = connectionThreads;
	}
	/**
	 * @param ioThreads the ioThreads to set
	 */
	public void setIoThreads(int ioThreads) {
		this.ioThreads = ioThreads;
	}
	/**
	 * initialize IoHandler when ioHandler is not set yet.
	 */
	private void initIoHandler() {
		if(ioHandler == null) {
			log.info("No WebSocket Io Handler associated - using defaults");
			ioHandler = new WebSocketHandler();
		}
	}
	/**
	 * start to listen ports;
	 */
	public void start() throws Exception{
		initIoHandler();
		Executor connectionExecutor = Executors.newFixedThreadPool(connectionThreads);
		Executor ioExecutor = Executors.newFixedThreadPool(ioThreads);
		acceptor = new NioSocketAcceptor(connectionExecutor, new NioProcessor(ioExecutor));
		
		acceptor.setHandler(ioHandler);
		SocketSessionConfig sessionConf = acceptor.getSessionConfig();
		sessionConf.setReuseAddress(true);
		sessionConf.setReceiveBufferSize(receiveBufferSize);
		sessionConf.setSendBufferSize(sendBufferSize);
		
		acceptor.setReuseAddress(true);
//		acceptor.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new WebSocketCodecFactory()));
		acceptor.bind(new InetSocketAddress(port));
		log.info("start web socket");
	}
	/**
	 * @param ioHandler the ioHandler to set
	 */
	public void setIoHandler(IoHandlerAdapter ioHandler) {
		this.ioHandler = ioHandler;
	}
	/**
	 * stop to listen ports;
	 */
	public void stop() {
		log.info("stop web socket");
		acceptor.unbind();
	}
}
