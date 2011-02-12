package com.ttProject.red5.server.plugin.websocket.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class WebSocketDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer message,
			ProtocolDecoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

}
