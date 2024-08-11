package com.mindray.egateway.hl7;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HL7ClientHandler extends IoHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(HL7ClientHandler.class);

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("HL7Client session created. {}", session.getRemoteAddress());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("HL7Client " + cause.getMessage(), cause);
		session.getCloseFuture().awaitUninterruptibly();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		//logger.debug("HL7Client received:" + message);

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("HL7Client session closed. ");
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		logger.debug("HL7Client send:" + message);
	}

}
