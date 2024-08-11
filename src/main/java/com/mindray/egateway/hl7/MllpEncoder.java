/**
 * 
 */
package com.mindray.egateway.hl7;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @author 50220398
 *
 */
public class MllpEncoder extends ProtocolEncoderAdapter {

	private String charset;

	public MllpEncoder(String charset) {
		this.charset = charset;
	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		CharsetEncoder encoder = Charset.forName(charset).newEncoder();
		IoBuffer buffer = IoBuffer.allocate(4096).setAutoExpand(true);
		buffer.put((byte) 0x0B);
		buffer.putString(message.toString(), encoder);
		buffer.put((byte) 0x1C);
		buffer.put((byte) 0x0D);
		buffer.flip();
		out.write(buffer);
	}

}
