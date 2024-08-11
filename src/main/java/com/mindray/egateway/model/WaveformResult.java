package com.mindray.egateway.model;

import java.util.HashMap;

public class WaveformResult {

	private String startTime;
	private String endTime;
	private String deviceId = "";
	
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

	private HashMap<String, Waveform> waveformValues = new HashMap<>();

	public void setStartTime(String time) {
		startTime = time;
	}

	public String getObserverTime() {
		return startTime;
	}

	public void setEndTime(String time) {
		endTime = time;
	}

	public String getEndTime() {
		return endTime;
	}

	public void addWaveform(String id, Waveform value) {
		waveformValues.put(id, value);
	}

	public void addWaveform(String id, String label, String sampleRate, String resolution, String invalidValue,
			String waveformSample) {
		Waveform value = new Waveform(label, sampleRate, resolution, invalidValue, waveformSample);
		addWaveform(id, value);
	}

	public Waveform getWaveform(String id) {
		if (waveformValues.containsKey(id)) {
			return waveformValues.get(id);
		} else {
			return null;
		}
	}

}
