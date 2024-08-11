/**
 * 
 */
package com.mindray.egateway.hl7;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author 50220398
 *
 */
public class HL7CodeFactory implements ProtocolCodecFactory {

	private MllpDecoder decoder;
	private MllpEncoder encoder;

	public HL7CodeFactory(String charset) {
		this.decoder = new MllpDecoder(charset);
		this.encoder = new MllpEncoder(charset);
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return this.encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return this.decoder;
	}

}
