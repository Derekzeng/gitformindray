package com.mindray.egateway;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import com.mindray.cis.third.IAdtQuery;
import com.mindray.cis.third.IClient;
import com.mindray.cis.third.IMDM;
import com.mindray.cis.third.IResult;
import com.mindray.egateway.hl7.HL7MessageSession;
import com.mindray.egateway.hl7.HL7Util;
import com.mindray.egateway.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


//@Component
@Deprecated
public class QueryProcessor extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(QueryProcessor.class);
	public static Vector<HL7MessageSession> sAdtQueryHL7MessageVector = new Vector<>();
	public static Vector<HL7MessageSession> sResultHL7MessageVector = new Vector<>();
	public static Vector<HL7MessageSession> sMdmHL7MessageVector = new Vector<>();
	//@Autowired
	private List<IClient> mQueryClient;
	private final AtomicBoolean mTerminated;
	private QueryProcessor() throws Exception {
		mTerminated = new AtomicBoolean(false);
	}
	public void terminate() {
		mTerminated.set(true);
		sAdtQueryHL7MessageVector.clear();
		sResultHL7MessageVector.clear();
		sMdmHL7MessageVector.clear();
	}
	@Override
	public void run() {
		while (!mTerminated.get()) {
			try {
				if (!sAdtQueryHL7MessageVector.isEmpty()) {
					HL7MessageSession messageSession = sAdtQueryHL7MessageVector.remove(0);
					new Thread(() -> processAdtMsg(messageSession)).start();
					//processAdtMsg(messageSession);
				} else if (!sResultHL7MessageVector.isEmpty()) {
					HL7MessageSession messageSession = sResultHL7MessageVector.remove(0);
					new Thread(() -> processResultMsg(messageSession)).start();
					//processResultMsg(messageSession);
				} else if (!sMdmHL7MessageVector.isEmpty()) {
					processMdmResultMsg();
				} else {
					try {
						sleep(2);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	//@Async("taskExecutor")
	protected void processAdtMsg(HL7MessageSession messageSession) {
		List<IClient> iClientStream = mQueryClient.stream().filter(m -> m instanceof IAdtQuery).collect(Collectors.toList());
		iClientStream.forEach(m -> {
            AdtRequest request = null;
			List<AdtResponse> list = null;
			String responseMessageString = null;
            try {
                request = HL7Util.getIns().ParseADTRequest(messageSession.message);
				list = ((IAdtQuery)m).querySync(request);
				responseMessageString = HL7Util.getIns().createAdtResponse(messageSession.message, list);
				messageSession.session.write(responseMessageString);
            } catch (HL7Exception | AdtQueryException |IOException e) {
				try {
					String ackMessage = HL7Util.getIns().createAck(messageSession.message, AcknowledgmentCode.AE);
					messageSession.session.write(ackMessage);
				} catch (HL7Exception | IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
            }
		});
	}
	//@Async("taskExecutor")
	protected void processResultMsg(HL7MessageSession messageSession) {
		List<IClient> iClientStream = mQueryClient.stream().filter(m -> m instanceof IResult).collect(Collectors.toList());
		iClientStream.forEach(m -> {
			String ackMessage = null;
			try {
				PatientResult result = HL7Util.getIns().parseRealtimeMessage(messageSession.message);
				ackMessage = HL7Util.getIns().createAck(messageSession.message);
				messageSession.session.write(ackMessage);
				((IResult)m).pushRealtimeResult(result);
			} catch (HL7Exception | IOException e) {
				logger.error(e.getMessage(), e);
				try {
					ackMessage = HL7Util.getIns().createAck(messageSession.message, AcknowledgmentCode.AE);
					messageSession.session.write(ackMessage);
				} catch (HL7Exception | IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});
	}
	//@Async("taskExecutor")
	protected void processMdmResultMsg() {
		HL7MessageSession messageSession = sMdmHL7MessageVector.remove(0);
		List<IClient> iClientStream = mQueryClient.stream().filter(m -> m instanceof IMDM).collect(Collectors.toList());
		iClientStream.forEach(m -> {
			String ackMessage = null;
			try {
				MdmResult result = HL7Util.getIns().parseMDMMessage(messageSession.message);
				logger.info("MDM msg Json is:\n{}", result.toString());
				((IMDM)m).pushMdmMsg(result);
				ackMessage = HL7Util.getIns().createAck(messageSession.message);
			} catch (HL7Exception | IOException e) {
				logger.error(e.getMessage(), e);
				try {
					ackMessage = HL7Util.getIns().createAck(messageSession.message, AcknowledgmentCode.AE);
				} catch (HL7Exception | IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
			messageSession.session.write(ackMessage);
		});
	}
}
