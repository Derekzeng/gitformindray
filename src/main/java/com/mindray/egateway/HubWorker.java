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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HubWorker {
    private static final Logger logger = LoggerFactory.getLogger(HubWorker.class);
    @Resource
    private List<IClient> mQueryClient;
    @Async("taskExecutor")
    public void processAdtMsg(HL7MessageSession messageSession) {
        logger.info("[mindray-info] enter processAdtMsg thread.");
        List<IClient> iClientStream = mQueryClient.stream().filter(m -> m instanceof IAdtQuery).collect(Collectors.toList());
        iClientStream.forEach(m -> {
            AdtRequest request = null;
            List<AdtResponse> list = null;
            String responseMessageString = null;
            try {
                request = HL7Util.getIns().ParseADTRequest(messageSession.message);
                list = ((IAdtQuery)m).querySync(request);
                responseMessageString = HL7Util.getIns().createAdtResponse(messageSession.message, list);
                logger.info("[mindray-info] processAdtMsg return {}",responseMessageString.replace("\r", "$@"));
                messageSession.session.write(responseMessageString);

            } catch (HL7Exception | AdtQueryException | IOException |NullPointerException e) {
                try {
                    String ackMessage = HL7Util.getIns().createAck(messageSession.message, AcknowledgmentCode.AE);
                    logger.info("[mindray-info] processADTMsg ackMessage return {}.",ackMessage.replace("\r", "$@"));
                    messageSession.session.write(ackMessage);

                } catch (HL7Exception | IOException | NullPointerException e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
        });
    }
    @Async("taskExecutor")
    public void processResultMsg(HL7MessageSession messageSession) {
        logger.info("[info] enter processResultMsg thread.");
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
    @Async("taskExecutor")
    public void processMdmResultMsg(HL7MessageSession messageSession) {
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

    @PostConstruct
    public void preConstruct(){
        logger.info("[mindray-info] HubWorker instance complete.");
    }
}
