package com.mindray.egateway.hl7;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mindray.egateway.model.AdtQueryException;
import com.mindray.egateway.model.AdtRequest;
import com.mindray.egateway.model.AdtResponse;
import com.mindray.egateway.model.AlarmResult;
import com.mindray.egateway.model.MdmResult;
import com.mindray.egateway.model.Msh;
import com.mindray.egateway.model.ParameterResult;
import com.mindray.egateway.model.ParameterValue;
import com.mindray.egateway.model.PatientResult;
import com.mindray.egateway.model.Pid;
import com.mindray.egateway.model.Pv1;
import com.mindray.egateway.model.Waveform;
import com.mindray.egateway.model.WaveformResult;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.GenericComposite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v26.datatype.NM;
import ca.uhn.hl7v2.model.v26.datatype.ST;
import ca.uhn.hl7v2.model.v26.datatype.XCN;
import ca.uhn.hl7v2.model.v26.group.ADR_A19_QUERY_RESPONSE;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v26.group.RSP_K21_QUERY_RESPONSE;
import ca.uhn.hl7v2.model.v26.message.ACK;
import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.ADT_A01;
import ca.uhn.hl7v2.model.v26.message.ADT_A02;
import ca.uhn.hl7v2.model.v26.message.ADT_A03;
import ca.uhn.hl7v2.model.v26.message.MDM_T01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.message.QBP_Q21;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
import ca.uhn.hl7v2.model.v26.message.RSP_K21;
import ca.uhn.hl7v2.model.v26.segment.EVN;
import ca.uhn.hl7v2.model.v26.segment.MSA;
import ca.uhn.hl7v2.model.v26.segment.MSH;
import ca.uhn.hl7v2.model.v26.segment.OBR;
import ca.uhn.hl7v2.model.v26.segment.OBX;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.model.v26.segment.PV1;
import ca.uhn.hl7v2.model.v26.segment.QAK;
import ca.uhn.hl7v2.model.v26.segment.QPD;
import ca.uhn.hl7v2.model.v26.segment.QRD;
import ca.uhn.hl7v2.model.v26.segment.TXA;
import ca.uhn.hl7v2.parser.Parser;

public class HL7Util {
	private static final Logger logger = LoggerFactory.getLogger(HL7Util.class);

	public static class PatIdGroup {
		public String pid;
		public String vid;
	}

	private static final String MessageType_RSP_K21 = "RSP_K21";
	private static final String Hd1_NamespaceID = "Mindray_eGateway_Proxy";
	private static final String Hd3_UniversalIDType = "EUI64";
	private static String Hd2_UniversalID;
	private static AtomicInteger hl7msgSeq;

	private static HL7Util sHL7Util = null;

	public static HL7Util getIns() {
		if (sHL7Util == null) {
			sHL7Util = new HL7Util();
		}
		return sHL7Util;
	}

	private HapiContext mContext = null;

	static {
		hl7msgSeq = new AtomicInteger();
		Hd2_UniversalID = "00A0370027022634";
	}

	private static int getHL7MsgSeq() {
		hl7msgSeq.incrementAndGet();
		int seq = hl7msgSeq.get();
		if (seq == Integer.MAX_VALUE) {
			hl7msgSeq.set(0);
		}
		return seq;
	}

	private HL7Util() {
		mContext = new DefaultHapiContext();
	}

	public String createAdtResponse(Message message, List<AdtResponse> list) throws HL7Exception, IOException {
		String hl7messageString = null;
		if (message instanceof QBP_Q21) {
			hl7messageString = createRSP_K21((QBP_Q21) message, list);
		} else if (message instanceof QRY_A19) {
			hl7messageString = createQRY_A19((QRY_A19) message, list);
		} else {
			logger.error("message error.");
		}
		return hl7messageString;
	}

	public String createRSP_K21(QBP_Q21 queryMessage, List<AdtResponse> list) throws HL7Exception, IOException {
		String encodeMsg = "";

		RSP_K21 rsp = new RSP_K21();
		rsp.initQuickstart("RSP", "K22", "P");
		// MSH Segment
		MSH mshSegment = rsp.getMSH();
		mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue(Hd1_NamespaceID);
		mshSegment.getMsh5_ReceivingApplication().getHd2_UniversalID().setValue(Hd2_UniversalID);
		mshSegment.getMsh5_ReceivingApplication().getHd3_UniversalIDType().setValue(Hd3_UniversalIDType);
		mshSegment.getMsh10_MessageControlID().setValue(Integer.toString(getHL7MsgSeq()));
		mshSegment.getMsh9_MessageType().getMsg3_MessageStructure().setValue(MessageType_RSP_K21);

		MSA msaSegment = rsp.getMSA();
		msaSegment.getMsa1_AcknowledgmentCode().setValue("AA");
		msaSegment.getMsa2_MessageControlID().setValue(queryMessage.getMSH().getMsh10_MessageControlID().getValue());

		QAK qakSegment = rsp.getQAK();
		qakSegment.getQak1_QueryTag().setValue(queryMessage.getQPD().getQpd2_QueryTag().getValue());
		if (list != null && !list.isEmpty()) {
			qakSegment.getQak2_QueryResponseStatus().setValue("OK");
		} else {
			qakSegment.getQak2_QueryResponseStatus().setValue("NF");
		}

		QPD qpdSegment = rsp.getQPD();
		qpdSegment.parse(queryMessage.getQPD().encode());

		int i = 0;
		for (AdtResponse response : list) {
			RSP_K21_QUERY_RESPONSE queryResponse = rsp.getQUERY_RESPONSE(i++);

			// PID segment
			PID pid = queryResponse.getPID();
			toHL7Seg(response.getPid(), pid);

			// PV1 segment
			String pv1Name = queryResponse.addNonstandardSegment("PV1");
			PV1 pv1 = (PV1) queryResponse.get(pv1Name);
			toHL7Seg(response.getPv1(), pv1);

			// OBR OBX segment
			AddObrObxSeg(response, queryResponse);
		}

		Parser parse = mContext.getPipeParser();
		encodeMsg = parse.encode(rsp);

		return encodeMsg;
	}

	public String createQRY_A19(QRY_A19 queryMessage, List<AdtResponse> list) throws HL7Exception, IOException {
		String encodeMsg = "";
		ADR_A19 rsp = new ADR_A19();
		rsp.initQuickstart("ADR", "A19", "P");
		// MSH Segment
		MSH mshSegment = rsp.getMSH();
		mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue(Hd1_NamespaceID);
		mshSegment.getMsh5_ReceivingApplication().getHd2_UniversalID().setValue(Hd2_UniversalID);
		mshSegment.getMsh5_ReceivingApplication().getHd3_UniversalIDType().setValue(Hd3_UniversalIDType);
		mshSegment.getMsh10_MessageControlID().setValue(Integer.toString(getHL7MsgSeq()));

		MSA msaSegment = rsp.getMSA();
		msaSegment.getMsa1_AcknowledgmentCode().setValue("AA");
		msaSegment.getMsa2_MessageControlID().setValue(queryMessage.getMSH().getMsh10_MessageControlID().getValue());

		QRD qrdSegment = rsp.getQRD();
		qrdSegment.parse(queryMessage.getQRD().encode());
		XCN[] xcns = qrdSegment.getQrd8_WhoSubjectFilter();
		if (xcns.length == 0) {
			qrdSegment.getQrd2_QueryFormatCode().setValue("D");
		}

		int i = 0;
		for (AdtResponse response : list) {
			ADR_A19_QUERY_RESPONSE queryResponse = rsp.getQUERY_RESPONSE(i++);

			// PID segment
			PID pid = queryResponse.getPID();
			toHL7Seg(response.getPid(), pid);

			// PV1 segment
			String pv1Name = queryResponse.addNonstandardSegment("PV1");
			PV1 pv1 = (PV1) queryResponse.get(pv1Name);
			toHL7Seg(response.getPv1(), pv1);

			// OBR OBX segment
			AddObrObxSeg(response, queryResponse);
		}

		Parser parse = mContext.getPipeParser();
		encodeMsg = parse.encode(rsp);

		return encodeMsg;
	}

	public void AddObrObxSeg(AdtResponse response, AbstractGroup queryResponse) throws HL7Exception {

		if (StringUtils.isEmpty(response.getWeight()) && StringUtils.isEmpty(response.getHeight()) && StringUtils.isEmpty(response.getBloodType())
				&& StringUtils.isEmpty(response.getPaced())) {
			return;
		}

		// OBR
		String obrName = queryResponse.addNonstandardSegment("OBR");
		OBR obr = (OBR) queryResponse.get(obrName);
		obr.getObr4_UniversalServiceIdentifier().getCwe1_Identifier().setValue("69952");
		obr.getObr4_UniversalServiceIdentifier().getCwe2_Text().setValue("MDC_DEV_MON_PT_PHYSIO_MULTI_PARAM");
		obr.getObr4_UniversalServiceIdentifier().getCwe3_NameOfCodingSystem().setValue("MDC");

		if (response.getWeight() != null && !response.getWeight().isEmpty()) {
			String obxName = queryResponse.addNonstandardSegment("OBX");
			OBX obx = (OBX) queryResponse.get(obxName);
			obx.getObx2_ValueType().setValue("NM");
			obx.getObx3_ObservationIdentifier().getCwe1_Identifier().setValue("68063");
			obx.getObx3_ObservationIdentifier().getCwe2_Text().setValue("MDC_ATTR_PT_WEIGHT");
			obx.getObx3_ObservationIdentifier().getCwe3_NameOfCodingSystem().setValue("MDC");
			obx.getObx4_ObservationSubID().setValue("1.10.1.68063");
			NM data = new NM(queryResponse.getMessage());
			data.setValue(response.getWeight());
			obx.getObx5_ObservationValue(0).setData(data);
			obx.getObx11_ObservationResultStatus().setValue("F");
		}

		if (response.getHeight() != null && !response.getHeight().isEmpty()) {
			String obxName = queryResponse.addNonstandardSegment("OBX");
			OBX obx = (OBX) queryResponse.get(obxName);
			obx.getObx2_ValueType().setValue("NM");
			obx.getObx3_ObservationIdentifier().getCwe1_Identifier().setValue("68060");
			obx.getObx3_ObservationIdentifier().getCwe2_Text().setValue("MDC_ATTR_PT_HEIGHT");
			obx.getObx3_ObservationIdentifier().getCwe3_NameOfCodingSystem().setValue("MDC");
			obx.getObx4_ObservationSubID().setValue("1.10.1.68060");
			NM data = new NM(queryResponse.getMessage());
			data.setValue(response.getHeight());
			obx.getObx5_ObservationValue(0).setData(data);
			obx.getObx11_ObservationResultStatus().setValue("F");
		}

		if (response.getBloodType() != null && !response.getBloodType().isEmpty()) {
			String obxName = queryResponse.addNonstandardSegment("OBX");
			OBX obx = (OBX) queryResponse.get(obxName);
			obx.getObx2_ValueType().setValue("ST");
			obx.getObx3_ObservationIdentifier().getCwe1_Identifier().setValue("2302");
			obx.getObx3_ObservationIdentifier().getCwe2_Text().setValue("MNDRY_ATTR_PT_BLOOD_TYPE");
			obx.getObx3_ObservationIdentifier().getCwe3_NameOfCodingSystem().setValue("99MNDRY");
			obx.getObx4_ObservationSubID().setValue("1.10.1.2302");
			ST data = new ST(queryResponse.getMessage());
			data.setValue(response.getBloodType());
			obx.getObx5_ObservationValue(0).setData(data);
			obx.getObx11_ObservationResultStatus().setValue("F");
		}

		if (response.getPaced() != null && !response.getPaced().isEmpty()) {
			String obxName = queryResponse.addNonstandardSegment("OBX");
			OBX obx = (OBX) queryResponse.get(obxName);
			obx.getObx2_ValueType().setValue("ST");
			obx.getObx3_ObservationIdentifier().getCwe1_Identifier().setValue("30459");
			obx.getObx3_ObservationIdentifier().getCwe2_Text().setValue("MNDRY_ATTR_PT_EVT_PACER_MODE");
			obx.getObx3_ObservationIdentifier().getCwe3_NameOfCodingSystem().setValue("99MNDRY");
			obx.getObx4_ObservationSubID().setValue("1.10.1.30459");
			ST data = new ST(queryResponse.getMessage());
			data.setValue(response.getPaced());
			obx.getObx5_ObservationValue(0).setData(data);
			obx.getObx11_ObservationResultStatus().setValue("F");
		}
	}

	public String createADT_A01(AdtResponse response) throws HL7Exception, IOException {
		String encodeMsg = "";
		ADT_A01 a01 = new ADT_A01();
		a01.initQuickstart("ADT", "A01", "P");
		// MSH Segment
		MSH mshSegment = a01.getMSH();
		mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue(Hd1_NamespaceID);
		mshSegment.getMsh5_ReceivingApplication().getHd2_UniversalID().setValue(Hd2_UniversalID);
		mshSegment.getMsh5_ReceivingApplication().getHd3_UniversalIDType().setValue(Hd3_UniversalIDType);
		mshSegment.getMsh10_MessageControlID().setValue(Integer.toString(getHL7MsgSeq()));

		// EVN Segment
		EVN evn = a01.getEVN();
		evn.getEvn1_EventTypeCode().setValue("A01");

		// PID Segment
		PID pid = a01.getPID();
		toHL7Seg(response.getPid(), pid);

		// PV1 segment
		String pv1Name = a01.addNonstandardSegment("PV1");
		PV1 pv1 = (PV1) a01.get(pv1Name);
		toHL7Seg(response.getPv1(), pv1);

		// OBR OBX
		AddObrObxSeg(response, a01);

		Parser parse = mContext.getPipeParser();
		encodeMsg = parse.encode(a01);

		return encodeMsg;
	}

	public String createADT_A02(AdtResponse response) throws HL7Exception, IOException {
		String encodeMsg = "";
		ADT_A02 a02 = new ADT_A02();
		a02.initQuickstart("ADT", "A02", "P");
		// MSH Segment
		MSH mshSegment = a02.getMSH();
		mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue(Hd1_NamespaceID);
		mshSegment.getMsh5_ReceivingApplication().getHd2_UniversalID().setValue(Hd2_UniversalID);
		mshSegment.getMsh5_ReceivingApplication().getHd3_UniversalIDType().setValue(Hd3_UniversalIDType);
		mshSegment.getMsh10_MessageControlID().setValue(Integer.toString(getHL7MsgSeq()));

		// EVN Segment
		EVN evn = a02.getEVN();
		evn.getEvn1_EventTypeCode().setValue("A02");

		// PID Segment
		PID pid = a02.getPID();
		toHL7Seg(response.getPid(), pid);

		// PV1 segment
		String pv1Name = a02.addNonstandardSegment("PV1");
		PV1 pv1 = (PV1) a02.get(pv1Name);
		toHL7Seg(response.getPv1(), pv1);

		// OBR OBX
		AddObrObxSeg(response, a02);

		Parser parse = mContext.getPipeParser();
		encodeMsg = parse.encode(a02);

		return encodeMsg;
	}

	public String createADT_A03(AdtResponse response) throws HL7Exception, IOException {
		String encodeMsg = "";
		ADT_A03 a03 = new ADT_A03();
		a03.initQuickstart("ADT", "A03", "P");
		// MSH Segment
		MSH mshSegment = a03.getMSH();
		mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue(Hd1_NamespaceID);
		mshSegment.getMsh5_ReceivingApplication().getHd2_UniversalID().setValue(Hd2_UniversalID);
		mshSegment.getMsh5_ReceivingApplication().getHd3_UniversalIDType().setValue(Hd3_UniversalIDType);
		mshSegment.getMsh10_MessageControlID().setValue(Integer.toString(getHL7MsgSeq()));

		// EVN Segment
		EVN evn = a03.getEVN();
		evn.getEvn1_EventTypeCode().setValue("A03");

		// PID Segment
		PID pid = a03.getPID();
		toHL7Seg(response.getPid(), pid);

		// PV1 segment
		String pv1Name = a03.addNonstandardSegment("PV1");
		PV1 pv1 = (PV1) a03.get(pv1Name);
		toHL7Seg(response.getPv1(), pv1);

		// OBR OBX
		AddObrObxSeg(response, a03);

		Parser parse = mContext.getPipeParser();
		encodeMsg = parse.encode(a03);

		return encodeMsg;
	}

	public String createADT_A08(AdtResponse response) throws HL7Exception, IOException {
		String encodeMsg = "";
		ADT_A02 a02 = new ADT_A02();
		a02.initQuickstart("ADT", "A08", "P");
		// MSH Segment
		MSH mshSegment = a02.getMSH();
		mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue(Hd1_NamespaceID);
		mshSegment.getMsh5_ReceivingApplication().getHd2_UniversalID().setValue(Hd2_UniversalID);
		mshSegment.getMsh5_ReceivingApplication().getHd3_UniversalIDType().setValue(Hd3_UniversalIDType);
		mshSegment.getMsh9_MessageType().getMsg3_MessageStructure().setValue("ADT_A08");
		mshSegment.getMsh10_MessageControlID().setValue(Integer.toString(getHL7MsgSeq()));

		// EVN Segment
		EVN evn = a02.getEVN();
		evn.getEvn1_EventTypeCode().setValue("A08");

		// PID Segment
		PID pid = a02.getPID();
		toHL7Seg(response.getPid(), pid);

		// PV1 segment
		String pv1Name = a02.addNonstandardSegment("PV1");
		PV1 pv1 = (PV1) a02.get(pv1Name);
		toHL7Seg(response.getPv1(), pv1);

		// OBR OBX
		AddObrObxSeg(response, a02);

		Parser parse = mContext.getPipeParser();
		encodeMsg = parse.encode(a02);

		return encodeMsg;
	}

	public String createAck(AbstractMessage src) throws HL7Exception, IOException {
		return createAck(src, AcknowledgmentCode.AA);
	}

	public String createAck(AbstractMessage src, AcknowledgmentCode theAcknowledgementCode) throws HL7Exception, IOException {
		Message out = src.generateACK(theAcknowledgementCode, null);
		ACK ack = (ACK) out;
		// MSH Segment
		MSH mshSegment = ack.getMSH();
		mshSegment.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue(Hd1_NamespaceID);
		mshSegment.getMsh5_ReceivingApplication().getHd2_UniversalID().setValue(Hd2_UniversalID);
		mshSegment.getMsh5_ReceivingApplication().getHd3_UniversalIDType().setValue(Hd3_UniversalIDType);
		mshSegment.getMsh10_MessageControlID().setValue(Integer.toString(getHL7MsgSeq()));
		Parser parse = mContext.getPipeParser();
		String encodeMsg = parse.encode(out);

		return encodeMsg;
	}

	public Message Parse(String hl7MessageString) throws HL7Exception {
		Parser p = mContext.getGenericParser();
		Message message = null;
		// hl7为没有R40， 转为R01处理
		if (hl7MessageString.contains("ORU^R40^ORU_R40")) {
			String temp = hl7MessageString.replace("ORU^R40^ORU_R40", "ORU^R01^ORU_R01");
			message = p.parse(temp);
			if (message instanceof ORU_R01) {
				ORU_R01 r01 = (ORU_R01) message;
				r01.getMSH().getMsh9_MessageType().getMsg1_MessageCode().setValue("ORU");
				r01.getMSH().getMsh9_MessageType().getMsg2_TriggerEvent().setValue("R40");
				r01.getMSH().getMsh9_MessageType().getMsg3_MessageStructure().setValue("ORU_R40");
			}
		} else {
			message = p.parse(hl7MessageString);
		}
		return message;
	}

	public String getMessageType(Message message) throws HL7Exception {
		AbstractMessage msg = (AbstractMessage) message;
		Structure mshStructure = msg.get("MSH");
		if (mshStructure != null) {
			MSH msh = (MSH) mshStructure;
			String t = msh.getMsh9_MessageType().getMsg1_MessageCode().getValue() + "^" + msh.getMsh9_MessageType().getMsg2_TriggerEvent().getValue();
			return t;
		} else {
			throw new HL7Exception("hl7 general message, cannot find MSH segment");
		}
	}

	public PatIdGroup getPidByHL7Message(String hl7MessageString) throws HL7Exception {
		PatIdGroup pidGroup = new PatIdGroup();
		Message message = this.Parse(hl7MessageString);
		if (message instanceof ORU_R01) {
			ORU_R01 r01 = (ORU_R01) message;
			PID pid = r01.getPATIENT_RESULT().getPATIENT().getPID();
			pidGroup.pid = pid.getPid3_PatientIdentifierList(0).getCx1_IDNumber().getValue();
			PV1 pv1 = (PV1) r01.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1();
			pidGroup.vid = pv1.getPv119_VisitNumber().getCx1_IDNumber().getValue();
			if (pidGroup.vid == null) {
				pidGroup.vid = "";
			}
		} else {
			throw new HL7Exception("message is not ORU_R01");
		}
		return pidGroup;
	}

	public AdtRequest ParseADTRequest(Message message) throws HL7Exception, AdtQueryException {
		AdtRequest request = null;
		if (message instanceof QBP_Q21) {
			QBP_Q21 adt = (QBP_Q21) message;
			Type[] composites = adt.getQPD().getField(3);
			HashMap<String, String> map = new HashMap<>();
			for (Type composite : composites) {
				if (((Varies) composite).getData() instanceof GenericComposite) {
					GenericComposite c = (GenericComposite) ((Varies) composite).getData();
					String key = c.getComponent(0).encode();
					String value = c.getComponent(1).encode();
					map.put(key, value);
				}
			}
			request = new AdtRequest();
			if (map.containsKey("@PID.3.1")) {
				String pid = map.get("@PID.3.1");
				request.setPid(pid);
			}
			if (map.containsKey("@PV1.19.1")) {
				String value = map.get("@PV1.19.1");
				request.setVid(value);
			}
			if (map.containsKey("@PID.5.2")) {
				String value = map.get("@PID.5.2");
				request.setFirstName(value);
			}
			if (map.containsKey("@PID.5.1.1")) {
				String value = map.get("@PID.5.1.1");
				request.setLastName(value);
			}
			if (map.containsKey("@PV1.3.3")) {
				String value = map.get("@PV1.3.3");
				request.setBed(value);
			}
			if (map.containsKey("@PV1.3.2")) {
				String value = map.get("@PV1.3.2");
				request.setRoom(value);
			}

		} else if (message instanceof QRY_A19) {
			QRY_A19 a19 = (QRY_A19) message;
			XCN[] xcns = a19.getQRD().getQrd8_WhoSubjectFilter();
			if (xcns.length > 1) {
				logger.warn("QRY_A19 request pid more than one.");
			}
			String pid = null;
			for (XCN xcn : xcns) {
				pid = xcn.getXcn1_IDNumber().encode();
				break;
			}
			if (StringUtils.isNotEmpty(pid)) {
				request = new AdtRequest();
				request.setPid(pid);
			} else {
				logger.warn("AdtRequest cannot find pid.");
			}

		} else {
			logger.error("message error.");
		}
		return request;
	}

	public static final class ObserverMsgType {
		public static final String ALARM = "196616^MDC_EVT_ALARM^MDC";
		public static final String PARAMETER = "182777000^monitoring of patient^SCT";
		public static final String WAVEFORM = "CONTINUOUS WAVEFORM";
		public static final String BOUNDED_WAVEFORM = "BOUNDED WAVEFORM";
		public static final String spot_check = "463890002^multiple physiological parameter spot-check analysis system^SCT";
	}

	private Msh fromHL7Seg(MSH msh) throws HL7Exception {
		Msh mshModel = new Msh();
		mshModel.setMsgDateTime(msh.getMsh7_DateTimeOfMessage().getValue());
		mshModel.setMsgTypeCode(msh.getMsh9_MessageType().getMsg1_MessageCode().getValue());
		mshModel.setMsgTypeTriggerEvent(msh.getMsh9_MessageType().getMsg2_TriggerEvent().getValue());
		mshModel.setMsgControlId(msh.getMsh10_MessageControlID().getValue());
		mshModel.setMsgSeqNumber(msh.getMsh13_SequenceNumber().getValue());
		return mshModel;
	}

	private Pid fromHL7Seg(PID pid) throws HL7Exception {
		Pid pidModel = new Pid();
		pidModel.setPid(pid.getPid3_PatientIdentifierList(0).getCx1_IDNumber().getValue());
		pidModel.setLastName(pid.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname().getValue());
		pidModel.setFirstName(pid.getPid5_PatientName(0).getXpn2_GivenName().getValue());
		pidModel.setMiddleName(pid.getPid5_PatientName(0).getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().getValue());

		pidModel.setBirthday(pid.getPid7_DateTimeOfBirth().getValue());
		pidModel.setGenderCode(pid.getPid8_AdministrativeSex().getValue());

		pidModel.setRace(pid.getPid10_Race(0).getIdentifier().getValue());
		pidModel.setAddress(pid.getPid11_PatientAddress(0).getXad1_StreetAddress().getSad1_StreetOrMailingAddress().toString());

		pidModel.setPostalCode(pid.getPid11_PatientAddress(0).getXad5_ZipOrPostalCode().getValue());
		pidModel.setHomePhone(pid.getPid13_PhoneNumberHome(0).getXtn1_TelephoneNumber().getValue());
		pidModel.setWorkPhone(pid.getPid14_PhoneNumberBusiness(0).getXtn1_TelephoneNumber().getValue());
		pidModel.setSsnNumber(pid.getPid19_SSNNumberPatient().getValue());

		pidModel.setCustomAttr1(pid.getPid35_SpeciesCode().getCwe1_Identifier().getValue());
		pidModel.setCustomName1(pid.getPid35_SpeciesCode().getCwe2_Text().getValue());
		pidModel.setCustomAttr2(pid.getPid36_BreedCode().getCwe1_Identifier().getValue());
		pidModel.setCustomName2(pid.getPid36_BreedCode().getCwe2_Text().getValue());
		pidModel.setCustomAttr3(pid.getPid38_ProductionClassCode().getCwe1_Identifier().getValue());
		pidModel.setCustomName3(pid.getPid38_ProductionClassCode().getCwe2_Text().getValue());
		pidModel.setCustomAttr4(pid.getPid39_TribalCitizenship(0).getCwe1_Identifier().getValue());
		pidModel.setCustomName4(pid.getPid39_TribalCitizenship(0).getCwe2_Text().getValue());
		return pidModel;
	}

	private void toHL7Seg(Pid pidData, PID pid) throws HL7Exception {
		pid.getPid3_PatientIdentifierList(0).getCx1_IDNumber().setValue(pidData.getPid());
		pid.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname().setValue(pidData.getLastName());
		pid.getPid5_PatientName(0).getXpn2_GivenName().setValue(pidData.getFirstName());
		pid.getPid5_PatientName(0).getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().setValue(pidData.getMiddleName());
		pid.getPid7_DateTimeOfBirth().setValue(pidData.getBirthday());
		pid.getPid8_AdministrativeSex().setValue(pidData.getGenderCode());
		pid.getPid10_Race(0).getCwe1_Identifier().setValue(pidData.getRace());
		pid.getPid11_PatientAddress(0).getXad1_StreetAddress().getSad1_StreetOrMailingAddress().setValue(pidData.getAddress());

		pid.getPid11_PatientAddress(0).getXad5_ZipOrPostalCode().setValue(pidData.getPostalCode());
		pid.getPid13_PhoneNumberHome(0).getXtn1_TelephoneNumber().setValue(pidData.getHomePhone());
		pid.getPid14_PhoneNumberBusiness(0).getXtn1_TelephoneNumber().setValue(pidData.getWorkPhone());

		pid.getPid19_SSNNumberPatient().setValue(pidData.getSsnNumber());

		pid.getPid35_SpeciesCode().getCwe1_Identifier().setValue(pidData.getCustumAttr1());
		pid.getPid35_SpeciesCode().getCwe2_Text().setValue(pidData.getCustomName1());

		pid.getPid36_BreedCode().getCwe1_Identifier().setValue(pidData.getCustomAttr2());
		pid.getPid36_BreedCode().getCwe2_Text().setValue(pidData.getCustomName2());

		pid.getPid38_ProductionClassCode().getCwe1_Identifier().setValue(pidData.getCustomAttr3());
		pid.getPid38_ProductionClassCode().getCwe2_Text().setValue(pidData.getCustomName3());

		pid.getPid39_TribalCitizenship(0).getCwe1_Identifier().setValue(pidData.getCustomAttr4());
		pid.getPid39_TribalCitizenship(0).getCwe2_Text().setValue(pidData.getCustomName4());
	}

	private Pv1 fromHL7Seg(PV1 pv1) throws HL7Exception {
		Pv1 pv1Model = new Pv1();
		pv1Model.setDepartment(pv1.getPv13_AssignedPatientLocation().getPl1_PointOfCare().getValue());
		pv1Model.setRoom(pv1.getPv13_AssignedPatientLocation().getPl2_Room().getValue());
		pv1Model.setBed(pv1.getPv13_AssignedPatientLocation().getPl3_Bed().getValue());
		pv1Model.setFacility(pv1.getPv13_AssignedPatientLocation().getPl4_Facility().getHd1_NamespaceID().getValue());
		pv1Model.setMacAddress(pv1.getPv13_AssignedPatientLocation().getPl9_LocationDescription().getValue());
		pv1Model.setAttendingDoctor(pv1.getPv17_AttendingDoctor(0).getXcn1_IDNumber().getValue());
		pv1Model.setReferringDoctor(pv1.getPv18_ReferringDoctor(0).getXcn1_IDNumber().getValue());
		// pv1Model.setConsultingDoctor(pv1.getPv19_ConsultingDoctor(0).getXcn2_FamilyName().getFn1_Surname().getValue());
		// pv1Model.setAdmittingDoctor(pv1.getPv117_AdmittingDoctor(0).getXcn2_FamilyName().getFn1_Surname().getValue());
		pv1Model.setPatientType(pv1.getPv118_PatientType().getValue());
		pv1Model.setVisitNumber(pv1.getPv119_VisitNumber().getCx1_IDNumber().getValue());
		pv1Model.setCustomAttr1(pv1.getPv142_PendingLocation().getPl1_PointOfCare().getValue());
		pv1Model.setCustomName1(pv1.getPv142_PendingLocation().getPl2_Room().getValue());
		pv1Model.setCustomAttr2(pv1.getPv143_PriorTemporaryLocation().getPl1_PointOfCare().getValue());
		pv1Model.setCustomName2(pv1.getPv143_PriorTemporaryLocation().getPl2_Room().getValue());
		pv1Model.setAdmitDate(pv1.getPv144_AdmitDateTime().getValue());
		pv1Model.setCustomAttr3(pv1.getPv150_AlternateVisitID().getCx1_IDNumber().getValue());
		pv1Model.setCustomName3(pv1.getPv150_AlternateVisitID().getCx2_IdentifierCheckDigit().getValue());
		pv1Model.setCustomAttr4(pv1.getPv152_OtherHealthcareProvider(0).getXcn1_IDNumber().getValue());
		pv1Model.setCustomName4(pv1.getPv152_OtherHealthcareProvider(0).getXcn2_FamilyName().getFn1_Surname().getValue());

		return pv1Model;
	}

	private void toHL7Seg(Pv1 pv1Data, PV1 pv1) throws HL7Exception {
		pv1.getPv12_PatientClass().setValue("I");
		pv1.getPv13_AssignedPatientLocation().getPl1_PointOfCare().setValue(pv1Data.getDepartment());
		pv1.getPv13_AssignedPatientLocation().getPl2_Room().setValue(pv1Data.getRoom());
		pv1.getPv13_AssignedPatientLocation().getPl3_Bed().setValue(pv1Data.getBed());
		pv1.getPv13_AssignedPatientLocation().getPl4_Facility().getHd1_NamespaceID().setValue(pv1Data.getFacility());
		pv1.getPv13_AssignedPatientLocation().getPl9_LocationDescription().setValue(pv1Data.getMacAddress());
		pv1.getPv17_AttendingDoctor(0).getXcn2_FamilyName().getFn1_Surname().setValue(pv1Data.getAttendingDoctor());
		pv1.getPv18_ReferringDoctor(0).getXcn2_FamilyName().getFn1_Surname().setValue(pv1Data.getReferringDoctor());
		// pv1.getPv19_ConsultingDoctor(0).getXcn2_FamilyName().getFn1_Surname().setValue(pv1Data.getConsultingDoctor());
		// pv1.getPv117_AdmittingDoctor(0).getXcn2_FamilyName().getFn1_Surname().setValue(pv1Data.getAdmittingDoctor());
		pv1.getPv118_PatientType().setValue(pv1Data.getPatientType());
		pv1.getPv119_VisitNumber().getCx1_IDNumber().setValue(pv1Data.getVisitNumber());
		pv1.getPv142_PendingLocation().getPl1_PointOfCare().setValue(pv1Data.getCustomAttr1());
		pv1.getPv142_PendingLocation().getPl2_Room().setValue(pv1Data.getCustomName1());
		pv1.getPv143_PriorTemporaryLocation().getPl1_PointOfCare().setValue(pv1Data.getCustomAttr2());
		pv1.getPv143_PriorTemporaryLocation().getPl2_Room().setValue(pv1Data.getCustomName2());
		pv1.getPv144_AdmitDateTime().setValue(pv1Data.getAdmitDate());
		pv1.getPv150_AlternateVisitID().getCx1_IDNumber().setValue(pv1Data.getCustomAttr3());
		pv1.getPv150_AlternateVisitID().getCx2_IdentifierCheckDigit().setValue(pv1Data.getCustomName3());
		pv1.getPv152_OtherHealthcareProvider(0).getXcn1_IDNumber().setValue(pv1Data.getCustomAttr4());
		pv1.getPv152_OtherHealthcareProvider(0).getXcn2_FamilyName().getFn1_Surname().setValue(pv1Data.getCustomName4());
	}

	private String getMainId(String subId) {
		// String[] idList = subId.split(".");
		int lastIndex = subId.lastIndexOf(".");
		return subId.substring(0, lastIndex);
	}

	public PatientResult parseRealtimeMessage(Message message) {

		PatientResult patientResult = new PatientResult();

		try {
			if (message instanceof ORU_R01) {
				processORUR01Msg(message, patientResult);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return patientResult;
	}

	public MdmResult parseMDMMessage(Message message) {
		MdmResult mdmT01 = new MdmResult();
		try {
			processMDMT01Msg(message, mdmT01);
		} catch (HL7Exception e) {
			logger.error(e.getMessage(), e);
		}
		return mdmT01;
	}

	private void processMDMT01Msg(Message message, MdmResult mdmT01) throws HL7Exception {
		MDM_T01 result_msg = (MDM_T01) message;
		MSH msh_seg = result_msg.getMSH();
		mdmT01.setMsh(fromHL7Seg(msh_seg));

		PID pid = result_msg.getPID();
		mdmT01.setPid(fromHL7Seg(pid));

		PV1 pv1 = result_msg.getPV1();
		mdmT01.setPv1(fromHL7Seg(pv1));

		TXA txaOriginal = result_msg.getTXA();
		String uniqueDocNumber = txaOriginal.getTxa12_UniqueDocumentNumber().encode(); // 12

		int pos = uniqueDocNumber.indexOf(DOC_SHARING_CONST.OBX_5_FILE_PATH_SUB_STR);
		if (-1 != pos) {
			mdmT01.setPath(uniqueDocNumber.substring(DOC_SHARING_CONST.OBX_5_FILE_PATH_SUB_STR.length() + pos));
		} else {
			mdmT01.setPath("");
			logger.error("MDM_T01 message OBX_5 file path is null.");
		}
	}

	private void processORUR01Msg(Message message, PatientResult patientResult) throws HL7Exception {
		ORU_R01 result_msg = (ORU_R01) message;
		MSH msh_seg = result_msg.getMSH();
		patientResult.setMsh(fromHL7Seg(msh_seg));

		ORU_R01_PATIENT_RESULT patient_result = result_msg.getPATIENT_RESULT();
		ORU_R01_PATIENT patient = patient_result.getPATIENT();

		PID pid = patient.getPID();
		patientResult.setPid(fromHL7Seg(pid));

		PV1 pv1 = patient.getVISIT().getPV1();
		patientResult.setPv1(fromHL7Seg(pv1));

		// ParameterResult paraResult = new ParameterResult();

		List<ORU_R01_ORDER_OBSERVATION> patient_obser_list = patient_result.getORDER_OBSERVATIONAll();
		for (ORU_R01_ORDER_OBSERVATION obser : patient_obser_list) {

			OBR obr = obser.getOBR();
			String obrType = obr.getObr4_UniversalServiceIdentifier().encode();

			if (obrType.equals(ObserverMsgType.PARAMETER) || obrType.equals(ObserverMsgType.spot_check)) {
				patientResult.setParameterResul(parseParameters(obser, patientResult));
				continue;
			}

			if (obrType.equals(ObserverMsgType.ALARM)) {

				patientResult.setAlarmResult(parseAlarms(obser));
				continue;
			}

			if (obrType.equals(ObserverMsgType.WAVEFORM) || obrType.equals(ObserverMsgType.BOUNDED_WAVEFORM)) {

				patientResult.setWaveformResul(parseWaveforms(obser));
				continue;
			}
		}
	}

	public static final class DOC_SHARING_CONST {
		public static final String OBX_2_VALUE_TYPE_RP = "RP";// 表示共享文件路径
		public static final String OBX_2_VALUE_TYPE_ED = "ED";// 表示共享文件内容
		public static final String OBX_4_SUB_ID = "1.39.1";
		public static final String OBX_5_FILE_PATH_SUB_STR = "File:";
		public static final int OBX_5_INDEX_4 = 4;
		public static final String OBX_5_PILI_CHAR = "\\^";
	}

	private ParameterResult parseParameters(ORU_R01_ORDER_OBSERVATION obser, PatientResult patientResult) throws HL7Exception {
		OBR obr = obser.getOBR();
		String obrType = obr.getObr4_UniversalServiceIdentifier().encode();
		if (!obrType.equals(ObserverMsgType.PARAMETER) && !obrType.equals(ObserverMsgType.spot_check)) {
			return null;
		}
		ParameterResult paraResult = new ParameterResult();
		paraResult.setObserverTime(obr.getObr7_ObservationDateTime().encode());
		List<ORU_R01_OBSERVATION> obx_list = obser.getOBSERVATIONAll();
		for (ORU_R01_OBSERVATION oru_r01_obx : obx_list) {
			OBX obx = oru_r01_obx.getOBX();
			String obx_1 = obx.getObx1_SetIDOBX().encode();
			if (obx_1.equals("1")) {
				paraResult.setDeviceId(obx.getObx18_EquipmentInstanceIdentifier(0).getEi1_EntityIdentifier().encode());
				paraResult.setDeviceTypeName(obx.getObx18_EquipmentInstanceIdentifier(0).getEi2_NamespaceID().encode());
				paraResult.setUserID(obx.getObx16_ResponsibleObserver(0).getXcn1_IDNumber().encode());
			}
			String obx_2 = obx.getObx2_ValueType().encode();
			if (obx_2.length() == 0) {
				continue;
			}
			String paraId = obx.getObx4_ObservationSubID().encode();
			String paraValue = "";
			if (0 != obx.getObx5_ObservationValueReps()) {
				paraValue = obx.getObx5_ObservationValue(0).encode();
			}
			String Producer_ID = obx.getObx16_ResponsibleObserver(0).encode();

			String paraUnit = obx.getObx6_Units().encode();
			String measureTime = obx.getObx14_DateTimeOfTheObservation().encode();

			ParameterValue para = new ParameterValue();
			para.setLabel(DataConfig.getIns().GetParaName(paraId));
			para.setValue(paraValue);
			para.setUnitId(paraUnit);
			para.setUnit(DataConfig.getIns().GetUnitName(paraUnit));
			para.setMeasureTime(measureTime);
			para.setProducer_ID(Producer_ID);
			paraResult.addParaValue(paraId, para);
		}

		return paraResult;
	}

	private AlarmResult parseAlarms(ORU_R01_ORDER_OBSERVATION obser) throws HL7Exception {

		OBR obr = obser.getOBR();
		String obrType = obr.getObr4_UniversalServiceIdentifier().encode();

		if (!obrType.equals(ObserverMsgType.ALARM)) {
			return null;
		}

		AlarmResult alarmResult = new AlarmResult();

		alarmResult.setObserverTime(obr.getObr7_ObservationDateTime().encode());

		List<ORU_R01_OBSERVATION> obx_list = obser.getOBSERVATIONAll();

		for (ORU_R01_OBSERVATION oru_r01_obx : obx_list) {
			OBX obx = oru_r01_obx.getOBX();

			String obx_1 = obx.getObx1_SetIDOBX().encode();
			if (obx_1.equals("1")) {
				alarmResult.setDeviceId(obx.getObx18_EquipmentInstanceIdentifier(0).getEi1_EntityIdentifier().encode());
			}

			String obx_2 = obx.getObx2_ValueType().encode();

			String id = obx.getObx3_ObservationIdentifier().encode();
			String subId = obx.getObx4_ObservationSubID().encode();

			String value = "";
			if (0 != obx.getObx5_ObservationValueReps()) {
				value = obx.getObx5_ObservationValue(0).encode();
			}

			String paraId = getMainId(subId);

			if (obx_2.length() == 0) {
				continue;
			}

			if (id.contains("MDC_EVT_ALARM")) {
				alarmResult.setOccureTime(obx.getObx14_DateTimeOfTheObservation().encode());
				String paraName = DataConfig.getIns().GetParaName(paraId);
				alarmResult.setParameterId(paraId);
				alarmResult.setParameterLabel(paraName);

				String alarmType = obx.getObx8_AbnormalFlags(0).encode();
				String alarmPriority = obx.getObx8_AbnormalFlags(1).encode();
				// 每个OBR下的obx中，只有第一条obx 的 obx-8.3表示告警的类型，生理SP 技术ST 提示SA
				String alarmSignal = obx.getObx8_AbnormalFlags(2).encode();
				alarmResult.setAlarmType(alarmType);
				alarmResult.setAlarmPriority(alarmPriority);
				alarmResult.setAlarmSignal(alarmSignal);
			}

			if (subId.equals(paraId + ".2")) {
				String unitId = obx.getObx6_Units().encode();
				String alarmLimit = obx.getObx7_ReferencesRange().encode();
				alarmResult.setParameterValue(value);

				alarmResult.setParameterUnitId(unitId);
				alarmResult.setParameterUnit(DataConfig.getIns().GetUnitName(unitId));
				alarmResult.setAlarmLimites(alarmLimit);
			}

			if (id.contains("MDC_ATTR_EVENT_PHASE")) {
				alarmResult.setAlarmPhrase(value);
			}

			if (id.contains("MDC_ATTR_ALARM_STATE")) {
				alarmResult.setAlarmState(value);
			}

		}

		return alarmResult;
	}

	private WaveformResult parseWaveforms(ORU_R01_ORDER_OBSERVATION obser) throws HL7Exception {

		OBR obr = obser.getOBR();
		// String obrType = obr.getObr4_UniversalServiceIdentifier().encode();

		WaveformResult waveResult = new WaveformResult();
		waveResult.setStartTime(obr.getObr7_ObservationDateTime().encode());
		waveResult.setEndTime(obr.getObr8_ObservationEndDateTime().encode());

		List<ORU_R01_OBSERVATION> obx_list = obser.getOBSERVATIONAll();

		for (ORU_R01_OBSERVATION oru_r01_obx : obx_list) {
			OBX obx = oru_r01_obx.getOBX();

			String obx_1 = obx.getObx1_SetIDOBX().encode();
			if (obx_1.equals("1")) {
				waveResult.setDeviceId(obx.getObx18_EquipmentInstanceIdentifier(0).getEi1_EntityIdentifier().encode());
			}

			String obx_2 = obx.getObx2_ValueType().encode();

			if (obx_2.length() == 0) {
				continue;
			}

			String waveId = obx.getObx3_ObservationIdentifier().encode();
			String subId = obx.getObx4_ObservationSubID().encode();

			String value = "";
			if (0 != obx.getObx5_ObservationValueReps()) {
				value = obx.getObx5_ObservationValue(0).encode();
			}
			// String unit = obx.getObx6_Units().encode();

			if (waveId.contains("MDC_ATTR_SAMP_RATE")) {
				String waveNameId = getMainId(subId);
				Waveform waveform = waveResult.getWaveform(waveNameId);
				if (waveform == null) {
					waveform = new Waveform();
					waveResult.addWaveform(waveNameId, waveform);
				}
				waveform.setSampleRate(value);

				continue;
			}

			if (waveId.contains("MDC_ATTR_NU_MSMT_RES")) {
				String waveNameId = getMainId(subId);
				Waveform waveform = waveResult.getWaveform(waveNameId);
				if (waveform == null) {
					waveform = new Waveform();
					waveResult.addWaveform(waveNameId, waveform);
				}
				waveform.setResolution(value);
				continue;
			}

			if (waveId.contains("MDC_EVT_INOP")) {
				String waveNameId = getMainId(subId);
				Waveform waveform = waveResult.getWaveform(waveNameId);
				if (waveform == null) {
					waveform = new Waveform();
					waveResult.addWaveform(waveNameId, waveform);
				}

				waveform.setInvalidValue(value);
				continue;
			}

			if (DataConfig.getIns().GetWaveName(subId).isEmpty() == false) {
				Waveform waveform = waveResult.getWaveform(subId);
				if (waveform == null) {
					waveform = new Waveform();
					waveResult.addWaveform(subId, waveform);
				}

				waveform.setLabel(DataConfig.getIns().GetWaveName(subId));
				waveform.setWaveformSample(value);
				// waveResult.addWaveform(subId, waveform);
			}
		}

		return waveResult;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws HL7Exception
	 */
//	public static void main(String[] args) throws HL7Exception, IOException {
//
//		HL7Util hl7util = HL7Util.getIns();
//
//		String MDM_T01 = "MSH|^~\\&|MINDRAY_EGATEWAY^00A03700273B61A9^EUI-64|MINDRAY|||20181226193432.0000+0800||MDM^T01^MDM_T01|49|P|2.6|||AL|NE||UNICODE UTF-8\r\n"
//				+ "SFT|Mindray|7.2.0|eGateway\r\n" + "EVN|T01|20181226193415.0000+0800\r\n"
//				+ "PID|||PatientID^^^Hospital^PI||LastName^FirstName^MiddleName^^^^L||19890707|M||2106-3\r\n"
//				+ "PV1||I|Department^Room^Bed^Facility||||^AttentdingDoctor|^ReferringDoctor|||||||||||VisitNumber\r\n"
//				+ "TXA|1|HP|AP|20181226193415.0000+0800||20181226193415.0000+0800||||||File://syn-PC/document/F140291051108_20181226193338006.xml|||||DO";
//
//		MdmResult msg1 = hl7util.parseMDMMessage(hl7util.Parse(MDM_T01));
//
//		logger.info("MDM_T01:" + msg1.toString());
//
//		HL7Client hl7Client = new HL7Client("UTF-8");
//		hl7Client.connect("192.168.100.23", 3402);
//
//		AdtResponse resp = new AdtResponse();
//
//		Pid pid = new Pid();
//		pid.setPid("pidtest");
//		pid.setFirstName("firstName");
//		pid.setLastName("lastName");
//		pid.setMiddleName("middleName");
//		pid.setGenderCode("F");
//		pid.setSsnNumber("SSN");
//
//		Pv1 pv1 = new Pv1();
//		// pv1.setAdmittingDoctor("admittingDoctor");
//		pv1.setAttendingDoctor("attendingDoctor");
//		pv1.setBed("bed");
//		// pv1.setConsultingDoctor("consultingDoctor");
//		pv1.setDepartment("department");
//		pv1.setFacility("facility");
//		pv1.setPatientType("A");
//		pv1.setReferringDoctor("referringDoctor");
//		pv1.setRoom("room");
//		pv1.setVisitNumber("visitNumber");
//
//		resp.setPid(pid);
//		resp.setPv1(pv1);
//
//		hl7Client.send(HL7Util.getIns().createADT_A01(resp));
//
//		try {
////			String msg = "MSH|^~\\&|MINDRAY_EGATEWAY^00A0370027E84537^EUI-64|MINDRAY|||20190521165625.0000+0800||QBP^ZV1^QBP_Q21|10|P|2.6|||AL|NE||UNICODE UTF-8\r\n"
////					+ "QPD|IHE PDQ Query|QueryTag_2|@PID.3.1^PID001~@PID.5.2^name~@PID.5.1.1^last~@PV1.3.3^bed~@PV1.3.2^room~@PV1.19.1^vid\r\n"
////					+ "RCP|I|50^RD\r\n";
////			String msg = "MSH|^~\\&|MINDRAY_EGATEWAY^00A0370027E84537^EUI-64|MINDRAY|||20190521170457.0000+0800||QBP^Q22^QBP_Q21|12|P|2.6|||AL|NE||UNICODE UTF-8\r\n"
////					+ "QPD|IHE PDQ Query|QueryTag_4|@PID.3.1^PID001~@PID.5.2^name~@PID.5.1.1^last\r\n"
////					+ "RCP|I|50^RD\r\n";
////
////			Message hapiMsg = hl7util.Paser(msg);
////			QBP_Q21 q21 = (QBP_Q21) hapiMsg;
////			String encodeMsg = hl7util.createRSP_K21(q21, response);
//
//			String msg = "MSH|^~\\&|MINDRAY_EGATEWAY^00A03700274F4F50^EUI-64|MINDRAY|||20181129160534.0000+0800||QRY^A19|25|P|2.6|||AL|NE||UNICODE UTF-8\r\n"
//					+ "QRD|20181129160534.0000+0800|D|D|25|||50^RD|pid001|RES|^eGateway \r\n";
//			Message hapiMsg = hl7util.Parse(msg);
//			String t = hl7util.createAck((AbstractMessage) hapiMsg);
//			System.out.println(t);
//			QRY_A19 a19 = (QRY_A19) hapiMsg;
//			List<AdtResponse> list = new LinkedList<AdtResponse>();
//			list.add(resp);
//			String encodeMsg = hl7util.createQRY_A19(a19, list);
//			System.out.println(encodeMsg);
////			String msg = "MSH|^~\\&|MINDRAY_EGATEWAY^00A0370027E84537^EUI-64|MINDRAY|||20190521170457.0000+0800||QBP^Q22^QBP_Q21|12|P|2.6|||AL|NE||UNICODE UTF-8\r\n" +
////					"QPD|IHE PDQ Query|QueryTag_4|@PID.3.1^PID001~@PID.5.2^name~@PID.5.1.1^last\r\n" +
////					"RCP|I|50^RD\r\n" +
////					"";
////
////			AdtRequest request = hl7util.Parse(hl7util.Parse(msg));
////			if(request != null) {
////				System.out.println(request.getPid());
////			}
//
////			String encodeMsg = hl7util.createADT_A08(response);
////			System.out.println(encodeMsg);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
