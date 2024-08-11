package com.mindray.egateway.model;

import java.util.HashMap;

public class ParameterResult {

	private String observerTime = "";
	private String deviceId = "";
	private String deviceTypeName = "";
	private String userID = "";
	private HashMap<String, ParameterValue> paraValues = new HashMap<>();

	public void setObserverTime(String time) {
		observerTime = time;
	}

	public String getObserverTime() {
		return observerTime;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the deviceTypeName
	 */
	public String getDeviceTypeName() {
		return deviceTypeName;
	}

	/**
	 * @param deviceTypeName the deviceTypeName to set
	 */
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void addParaValue(String id, ParameterValue para) {
		paraValues.put(id, para);
	}

	public ParameterValue getParameter(String paraId) {
		if (paraValues.containsKey(paraId)) {
			return paraValues.get(paraId);
		} else {
			ParameterValue para = new ParameterValue();
			return para;
		}

	}

}
