package com.ttProject.red5.server.plugin.websocket;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * WebSocketConnection
 * <pre>
 * data for connection.
 * </pre>
 */
public class WebSocketConnection {
	private boolean connected = false;
	private IoSession session;
	private String host;
	private String path;
	private String origin;
	private String version = null;
	private String query;
	private List<IoBuffer> bufferList = new ArrayList<IoBuffer>();
	/**
	 * constructor
	 */
	public WebSocketConnection(IoSession session) {
		connected = false;
		this.session = session;
	}
	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}
	/**
	 * on connected, put flg and clear keys.
	 */
	public void setConnected() {
		connected = true;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return "ws://" + host + path;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}
	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	/**
	 * @return the session
	 */
	public IoSession getSession() {
		return session;
	}
	/**
	 * @return path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		if(path.length() != 0 && path.charAt(path.length()-1) == '/') {
			this.path = path.substring(0, path.length()-1);
		}
		else {
			this.path = path;
		}
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	/**
	 * get the connection id
	 * @return id
	 * @throws WebSocketException when the session is invalid...
	 */
	public long getId() throws WebSocketException{
		if(session == null) {
			throw new WebSocketException("invalid connection");
		}
		return session.getId();
	}
	public WebSocketScope getScope() {
		return new WebSocketScopeManager().getScope(getPath());
	}
	/**
	 * sendmessage to client
	 * @param buffer IoBuffer data
	 */
	public void send(IoBuffer buffer) {
		session.write(buffer);
	}
	/**
	 * sendmessage to client
	 * @param data string data
	 * @throws UnsupportedEncodingException 
	 */
	public void send(String string) throws UnsupportedEncodingException{
		byte[] data = string.getBytes("UTF8");
		int size = data.length;
		if(version == null) {
			// hybi00
			IoBuffer buffer = IoBuffer.allocate(size + 4);
			buffer.put((byte)0x00);
			buffer.put(data);
			buffer.put((byte)0xFF);
			buffer.flip();
			session.write(buffer);
		}
		else {
			// rfc6455
			IoBuffer buffer = IoBuffer.allocate(size + 8);
			buffer.put((byte)0x81);
			// size
			if(size < 126) {
				// 8bit
				buffer.put((byte)(0x80 | size));
			}
			else if(size < 0x010000) {
				// 16bit
				buffer.put((byte)0xFE);
				buffer.putShort((short)size);
			}
			else {
				// 64bit
				buffer.put((byte)0xFF);
				buffer.putLong(size);
			}
			// make up mask bytes
			int maskInt = new Random().nextInt();
			byte[] b = new byte[4];
			b[0] = (byte)((maskInt >> 24) & 0xFF);
			b[1] = (byte)((maskInt >> 16) & 0xFF);
			b[2] = (byte)((maskInt >> 8) & 0xFF);
			b[3] = (byte)(maskInt & 0xFF);
			buffer.put(b);
			for(int i=0;i < size;i ++) {
				buffer.put((byte)(b[(i % 4)] ^ data[i]));
			}
			buffer.flip();
			session.write(buffer);
		}
	}
	/**
	 * receive message
	 * @param buffer
	 */
	public void receiveData(IoBuffer buffer) {
		if(isConnected()) {
			// check if closing order
			if(checkClosing(buffer)) {
				return;
			}
			// accept as string message.
			analizeData(buffer);
		}
		else {
			WebSocketHandshake handshake = new WebSocketHandshake(this);
			try {
				handshake.handShake(buffer);
			}
			catch(WebSocketException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * analize data as string and send it to scope.
	 */
	public void analizeData(IoBuffer buffer) {
		if(version == null) {
			// hybi00
			if(buffer.get() == 0x00) { // check only front byte
				int size = buffer.capacity() - 2;
				IoBuffer result = IoBuffer.allocate(size + 4);
				for(int i = 0;i < size;i ++) {
					byte data = buffer.get();
					if(data == (byte)0xFF) { // if hit the end of byte, quit the loop.
						break;
					}
					result.put(data);
				}
				result.flip();
				try {
					getScope().receiveData(this, new String(result.array(), "UTF-8"));
				}
				catch (Exception e) {
				}
			}
		}
		else {
			// rfc6455
			byte first = buffer.get();
			if((first & 0x0F) == 0x01) { // accept only string
				byte second = buffer.get();
				// get data size
				long size;
				if((second & 0x7F) == 0x7E) {
					size = buffer.getShort();
				}
				else if((second & 0x7F) == 0x7F){
					size = buffer.getLong();
				}
				else {
					size = second & 0x7F;
				}
				IoBuffer string = IoBuffer.allocate((int)size);
				if((second & 0x80) != 0x00) {
					byte[] mask = new byte[4];
					mask[0] = buffer.get();
					mask[1] = buffer.get();
					mask[2] = buffer.get();
					mask[3] = buffer.get();
					for(int i=0;i < size;i ++) {
						string.put((byte)(mask[(i % 4)] ^ buffer.get()));
					}
				}
				else {
					for(int i=0;i < size;i ++) {
						string.put(buffer.get());
					}
				}
				bufferList.add(string);
				if((first & 0x80) != 0x00) {
					// make data string;
					size = 0;
					for(IoBuffer buf : bufferList) {
						size += buf.capacity();
					}
					IoBuffer result = IoBuffer.allocate((int)size);
					for(IoBuffer buf : bufferList) {
						result.put(buf.flip());
					}
					try {
						getScope().receiveData(this, new String(result.array(), "UTF-8"));
					}
					catch (Exception e) {
					}
					bufferList.clear();
				}
			}
		}
	}
	/**
	 * handle the closing.
	 * @param buffer
	 * @return true:for closing.
	 */
	public boolean checkClosing(IoBuffer buffer) {
		byte data = buffer.get(0);
		if(version == null) {
			// hybi00
			if(data == (byte)0xFF) {
				System.out.println(buffer.getHexDump());
				close();
				return true;
			}
		}
		else {
			// rfc6455
			if((data & 0x08) != 0x00) {
				System.out.println(buffer.getHexDump());
				close();
				return true;
			}
		}
		return false;
	}
	/**
	 * close Connection
	 */
	public void close() {
		if(!connected) {
			return;
		}
		WebSocketScopeManager manager = new WebSocketScopeManager();
		manager.removeConnection(this);
		connected = false;
		session.close(true);
	}
}
