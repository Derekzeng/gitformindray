package com.mindray.egateway.model;

import org.apache.commons.lang3.StringUtils;
import com.google.gson.Gson;


public class AdtResponse {
	private Pid pid;
	private Pv1 pv1;
	private String height;
	private String weight;
	private String bloodType;
	private String paced;

	public Pid getPid() {
		return pid;
	}

	public void setPid(Pid pid) {
		this.pid = pid;
	}

	public Pv1 getPv1() {
		return pv1;
	}

	public void setPv1(Pv1 pv1) {
		this.pv1 = pv1;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getBloodType() {
		return bloodType;
	}

	public void setBloodType(String bloodType) {
		if (!StringUtils.isEmpty(bloodType) && (bloodType.equalsIgnoreCase("A") || paced.equalsIgnoreCase("B")
				|| bloodType.equalsIgnoreCase("AB") || paced.equalsIgnoreCase("O") || bloodType.equalsIgnoreCase("NA")
				|| bloodType.equalsIgnoreCase("Unknown"))) {
			this.bloodType = bloodType;

		} else {
			this.bloodType = "";
		}
	}

	public String getPaced() {
		return paced;
	}

	// ON OFF
	public void setPaced(String paced) {
		if (!StringUtils.isEmpty(paced) && (paced.equalsIgnoreCase("ON") || paced.equalsIgnoreCase("OFF"))) {
			this.paced = paced;
		} else {
			this.paced = "";
		}
	}

	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
