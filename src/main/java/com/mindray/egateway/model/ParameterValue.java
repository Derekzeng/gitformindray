package com.mindray.egateway.model;

public class ParameterValue {
	private String label;
	private String value;
	private String unitId;
	private String unit;
	private String measureTime;
	private String Producer_ID;
    
	public ParameterValue() {

	}

//	public ParameterValue(String label, String value, String unit, String time) {
//		this.label = label;
//		this.value = value;
//		this.unit = unit;
//		this.measureTime = time;
//	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String value) {
		label = value;
	}

	public String getValue() {
		return value;
	}
	public String getProducer_ID() {
		return  Producer_ID;
	}
	public void setProducer_ID(String Producer_ID){
		this.Producer_ID = Producer_ID;
	}
	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String value) {
		unit = value;
	}

	public String getMeasureTime() {
		return measureTime;
	}

	public void setMeasureTime(String value) {
		measureTime = value;
	}

}
