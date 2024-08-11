package com.mindray.webservice.adt.impl;

import com.alibaba.fastjson.JSONObject;
import com.mindray.egateway.hl7.HL7Client;
import com.mindray.egateway.hl7.HL7Util;
import com.mindray.egateway.model.AdtResponse;
import com.mindray.egateway.model.Pid;
import com.mindray.egateway.model.Pv1;
import com.mindray.webservice.adt.Manage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.jws.WebService;
import java.text.SimpleDateFormat;

@Component
@WebService
public class ManageImpl implements Manage {
	private static final Logger logger = LoggerFactory.getLogger(ManageImpl.class);
	@Resource
	private HL7Client hl7Client;

	@Override
	public String manageAdt(String callType, String message) {
		logger.info("Executing operation manageAdt:");
		logger.info("Input callType:" + callType);// A01（接收）、A03（解除）、A08（更新）
		logger.info("Input message:" + message);
		try {
			AdtResponse response = new AdtResponse();
			String error = toADTResponse(message, response);
			if (StringUtils.isBlank(error) && callType.equalsIgnoreCase("A01")) {
				String hl7message = HL7Util.getIns().createADT_A01(response);
				hl7Client.send(hl7message);
			} else if (StringUtils.isBlank(error) && callType.equalsIgnoreCase("A03")) {
				String hl7message = HL7Util.getIns().createADT_A03(response);
				hl7Client.send(hl7message);
			} else if (StringUtils.isBlank(error) && callType.equalsIgnoreCase("A08")) {
				String hl7message = HL7Util.getIns().createADT_A08(response);
				hl7Client.send(hl7message);
			}
			return getResponseMsgInner(response, error);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private String toADTResponse(String message, AdtResponse response) {
		try {
			String error = "";
			Pid pid = new Pid();
			Pv1 pv1 = new Pv1();
			JSONObject jsonObject = JSONObject.parseObject(message);
			if (jsonObject != null) {
				pid.setPid(jsonObject.getString("pid"));
				pv1.setVisitNumber(jsonObject.getString("vid"));
				pid.setFirstName(jsonObject.getString("name"));
				String Sex = jsonObject.getString("gender");
				if ("1".equals(Sex)) {
					pid.setGenderCode("M");
				} else if ("2".equals(Sex)) {
					pid.setGenderCode("F");
				}
				String DateOfBirth = jsonObject.getString("dob");
				try {
					if (StringUtils.isNotBlank(DateOfBirth) && DateOfBirth.length() >= 8) {
						DateOfBirth = DateOfBirth.substring(0, 8);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						sdf.setLenient(false);
						sdf.parse(DateOfBirth);
						pid.setBirthday(DateOfBirth);
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				pv1.setBed(jsonObject.getString("bed"));
				pv1.setRoom(jsonObject.getString("room"));
				pv1.setDepartment(jsonObject.getString("department"));
				pv1.setFacility(jsonObject.getString("facility"));
				pv1.setMacAddress(jsonObject.getString("deviceid"));
				pv1.setReferringDoctor(jsonObject.getString("doctor"));// 管床医生
				response.setPid(pid);
				response.setPv1(pv1);
				response.setHeight(jsonObject.getString("height"));
				response.setWeight(jsonObject.getString("weight"));

			}
			return error;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return e.toString();
		}
	}

	private String getResponseMsgInner(AdtResponse response, String errorMsg) throws Exception {
		String xml_response = "";
		try {
			if (StringUtils.isEmpty(errorMsg)) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("message", "成功");
				jsonObject.put("status", "OK");
				xml_response = jsonObject.toJSONString();
			} else {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("message", errorMsg);
				jsonObject.put("status", "error");
				xml_response = jsonObject.toJSONString();
			}
		} catch (Exception e) {
			xml_response = e.getMessage();
		}
		logger.info("返回消息为：\n" + xml_response);
		return xml_response;
	}
}
