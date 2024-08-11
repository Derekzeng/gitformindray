/**
 * OBR
 */
package com.mindray.egateway.model;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author 50220398
 *
 */
public class Obr {
	public final static String OBR_PARAM = "182777000^monitoring of patient^SCT";// 参数
	public final static String OBR_WAVEFORM = "CONTINUOUS WAVEFORM"; // 实时波形
	public final static String OBR_ALARM_WAVEFORM = "BOUNDED WAVEFORMS"; // 报警相关波形
	public final static String OBR_ALARM = "196616^MDC_EVT_ALARM^MDC"; // 报警
	private String id;
	private String universalIdentifier;
	private String priority;
	private String requestDateTime;
	private String observationDateTime;
	private List<Obx> obxs = new LinkedList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniversalIdentifier() {
		return universalIdentifier;
	}

	public void setUniversalIdentifier(String universalIdentifier) {
		this.universalIdentifier = universalIdentifier;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getRequestDateTime() {
		return requestDateTime;
	}

	public void setRequestDateTime(String requestDateTime) {
		this.requestDateTime = requestDateTime;
	}

	public String getObservationDateTime() {
		return observationDateTime;
	}

	public void setObservationDateTime(String observationDateTime) {
		this.observationDateTime = observationDateTime;
	}

	public List<Obx> getObxs() {
		return obxs;
	}
}
