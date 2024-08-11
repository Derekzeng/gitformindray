package com.mindray.egateway.model;

import org.apache.commons.lang3.StringUtils;

public class AlarmResult {
//	'observe_time': '20200227161331.0000+0800', 'parameter_id': '1.2.1.150344', 'parameter_label': 'Temperature 1', 'occur_time': '20200227161331.0000+0800', 'alarm_type': 'notify', 'alarm_priority': 'none_priority', 'alarm_signal': 'notify', 'parameter_value': '95.0', 'parameter_unit': 'Degrees Fahrenheit', 
//	'alarm_limit': '96.8-102.2', 'phase': 'end', 'state': 'inactive'},

	public class AlarmType {
		// 对应的HL里面的字符
		public static final String HIGH = "high"; // H
		public static final String LOW = "low"; // L
		public static final String EXTREMELY = "extremely"; // E
		public static final String OTHER = "other"; // A
		public static final String NOTIFY = "notify"; // N

		private AlarmType() {
		}
	}

	public class AlarmPriority {
		public static final String NONE = "none"; // PN
		public static final String LOW = "low"; // PL
		public static final String MIDDLE = "middle"; // PM
		public static final String HIGHT = "high"; // PH

		private AlarmPriority() {

		}

	}

	public class AlarmSignal {
		public static final String PHYSIOLOGICAL = "physiological"; // SP
		public static final String TECHNOLOGY = "technology"; // ST
		public static final String NOTIFY = "notify"; // SA

		private AlarmSignal() {
		}
	}

	private String deviceId = "";
	private String observerTime;
	private String occureTime;
	private String alarmType; // AlarmType
	private String alarmPriority; // AlarmPriority
	private String alarmSignal; // AlarmSignal
	private String parameterId;
	private String parameterLable;
	private String parameterValue;
	private String parameterUnitId;
	private String parameterUnit;
	private String alarmLimites;
	private String alarmPhrase; // start, end, escalate,reset,inactivation,acknowledged
	private String alarmState; // inactive, active, latched

	
	
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

	public String getObserverTime() {
		return observerTime;
	}

	public void setObserverTime(String time) {
		observerTime = time;
	}

	public String getOccureTime() {
		return occureTime;
	}

	public void setOccureTime(String time) {
		occureTime = time;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String type) {
		if (type.equals("H")) {
			alarmType = AlarmType.HIGH;
		}

		if (type.equals("L")) {
			alarmType = AlarmType.LOW;
		}

		if (type.equals("E")) {
			alarmType = AlarmType.EXTREMELY;
		}

		if (type.equals("A")) {
			alarmType = AlarmType.OTHER;
		}

		if (type.equals("N")) {
			alarmType = AlarmType.NOTIFY;
		}
	}

	public String getAlarmPriority() {
		return alarmPriority;
	}

	public void setAlarmPriority(String priority) {
		if (priority.equals("PN")) {
			alarmPriority = AlarmPriority.NONE;
		}

		if (priority.equals("PL")) {
			alarmPriority = AlarmPriority.LOW;
		}

		if (priority.equals("PM")) {
			alarmPriority = AlarmPriority.MIDDLE;
		}

		if (priority.equals("PH")) {
			alarmPriority = AlarmPriority.HIGHT;
		}
	}

	public String getAlarmSignal() {
		return alarmSignal;
	}

	public void setAlarmSignal(String signal) {

		if (signal.equals("SP")) {
			alarmSignal = AlarmSignal.PHYSIOLOGICAL;
		}

		if (signal.equals("ST")) {
			alarmSignal = AlarmSignal.TECHNOLOGY;
		}

		if (signal.equals("SA")) {
			alarmSignal = AlarmSignal.NOTIFY;
		}

	}

	public String getParameterLabel() {
		return parameterLable;
	}

	public void setParameterLabel(String label) {
		parameterLable = label;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String id) {
		parameterId = id;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String value) {

		if (alarmSignal.equalsIgnoreCase(AlarmSignal.PHYSIOLOGICAL)) {
			parameterValue = value;
		} else if (alarmSignal.equalsIgnoreCase(AlarmSignal.NOTIFY) && !StringUtils.isEmpty(value)
				&& !value.contains("MDC_DEV_SYS_ANESTH_MDS") && !value.contains("MDC_DEV_REGUL_VOL_VENT_MDS")
				&& !value.contains("MDC_DEV_MON_PT_PHYSIO_MULTI_PARAM_MDS")) {
			parameterValue = value;
		} else {
			parameterValue = "";
		}
	}

	public String getParameterUnitId() {
		return parameterUnitId;
	}

	public void setParameterUnitId(String parameterUnitId) {
		this.parameterUnitId = parameterUnitId;
	}

	public String getParameterUnit() {
		return parameterUnit;
	}

	public void setParameterUnit(String unit) {
		parameterUnit = unit;
	}

	public String getAlarmLimites() {
		return alarmLimites;
	}

	public void setAlarmLimites(String limites) {
		alarmLimites = limites;
	}

	public String getAlarmPhrase() {
		return alarmPhrase;
	}

	public void setAlarmPhrase(String phrase) {
		alarmPhrase = phrase;
	}

	public String getAlarmState() {
		return alarmState;
	}

	public void setAlarmState(String state) {
		alarmState = state;
	}

}
