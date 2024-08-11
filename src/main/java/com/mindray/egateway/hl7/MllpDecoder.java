/**
 * 
 */
package com.mindray.egateway.hl7;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 50220398
 *
 */
public class MllpDecoder extends CumulativeProtocolDecoder {

	private static final Logger logger = LoggerFactory.getLogger(MllpDecoder.class);
	private static final int ERROR_LOCATION = -1;

	private CharsetDecoder charsetDecode;

	public MllpDecoder(String charsetString) {
		Charset charset = Charset.forName(charsetString);
		charsetDecode = charset.newDecoder();
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		int start = in.position();
		int sbPos = ERROR_LOCATION;
		int ebPos = ERROR_LOCATION;
		byte prev = 0;
		while (in.hasRemaining()) {
			byte current = in.get();
			if (current == 0x0B) {
				sbPos = in.position();
			}
			if (prev == 0x1C && current == 0x0D) {
				ebPos = in.position();
				try {
					if (sbPos != ERROR_LOCATION) {
						int length = ebPos - sbPos - 2;
						IoBuffer buf = in.getSlice(sbPos, length);
						String message = buf.getString(charsetDecode);
						out.write(message);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				return true;
			}
			prev = current;
		}
		in.position(start);
		return false;
	}

}
