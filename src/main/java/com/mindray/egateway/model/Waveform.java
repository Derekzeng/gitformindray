package com.mindray.egateway.model;

public class Waveform {
	private String label;
	private String sampleRate;
	private String resolution;
	private String invalidValue;
	private String waveformSample;

	public Waveform(String lab, String samRate, String res, String invad, String wavSample) {
		label = lab;
		sampleRate = samRate;
		resolution = res;
		;
		invalidValue = invad;
		waveformSample = wavSample;
	}

	public Waveform() {

	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String value) {
		label = value;
	}

	public String getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(String value) {
		sampleRate = value;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String value) {
		resolution = value;
	}

	public String getInvalidValue() {
		return invalidValue;
	}

	public void setInvalidValue(String value) {
		invalidValue = value;
	}

	public String getWaveformSample() {
		return waveformSample;
	}

	public void setWaveformSample(String value) {
		waveformSample = value;
	}

}
