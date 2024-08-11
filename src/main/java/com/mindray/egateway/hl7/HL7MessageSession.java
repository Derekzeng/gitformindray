/**
 * 
 */
package com.mindray.egateway.hl7;

import org.apache.mina.core.session.IoSession;

import ca.uhn.hl7v2.model.AbstractMessage;

/**
 * @author 50220398
 *
 */
public class HL7MessageSession {
	public AbstractMessage message;
	public IoSession session;

	public HL7MessageSession(AbstractMessage message, IoSession session) {
		this.message = message;
		this.session = session;
	}

	public HL7MessageSession(AbstractMessage message, IoSession session, String messageString) {
		this.message = message;
		this.session = session;
	}
}
