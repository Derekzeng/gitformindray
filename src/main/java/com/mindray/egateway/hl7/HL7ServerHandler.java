package com.mindray.egateway.hl7;

import com.mindray.egateway.HubWorker;
import com.mindray.egateway.SpringBeanUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.naming.factory.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v26.message.ACK;
import ca.uhn.hl7v2.model.v26.message.MDM_T01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.message.QBP_Q21;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;


public class HL7ServerHandler extends IoHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(HL7ServerHandler.class);

	private final HL7Util mHL7Util;

	public HL7ServerHandler() {
		mHL7Util = HL7Util.getIns();
		hubWorker = SpringBeanUtils.getBean(HubWorker.class);
	}

	private final HubWorker hubWorker;

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("HL7Server session created. {}", session.getRemoteAddress());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("HL7Server " + cause.getMessage(), cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		try {
			logger.info("message received :{}", message.toString());
			long start = System.currentTimeMillis();
			Message hl7Message = mHL7Util.Parse(message.toString());
			long end = System.currentTimeMillis();
			logger.info("parse message cost :{}", end - start);
			HL7MessageSession hl7Msg = new HL7MessageSession((AbstractMessage) hl7Message, session);
			if (hl7Message instanceof QBP_Q21 || hl7Message instanceof QRY_A19) {
				//QueryProcessor.sAdtQueryHL7MessageVector.add(hl7Msg);
				hubWorker.processAdtMsg(hl7Msg);
			} else if (hl7Message instanceof ORU_R01) {
				//QueryProcessor.sResultHL7MessageVector.add(hl7Msg);
				hubWorker.processResultMsg(hl7Msg);
			} else if (hl7Message instanceof MDM_T01) {
				//QueryProcessor.sMdmHL7MessageVector.add(hl7Msg);
				hubWorker.processMdmResultMsg(hl7Msg);
			} else if (hl7Message instanceof ACK) {
				logger.debug("hl7 message: ack");
			}
		} catch (HL7Exception e) {
			//logger.error(e.getMessage(), e);
			logger.error("Parse error, ml7 message: {},errors:{}", message,e.getMessage());
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("HL7Server session closed. {}", session.getRemoteAddress());
	}
}
