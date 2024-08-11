package com.mindray.egateway.model;

public class Pv1 {
	private String department; // 1.3.1
	private String room; // 1.3.2
	private String bed; // 1.3.3
	private String facility; // 1.3.4
	private String macAddress; //3.9
	private String attendingDoctor; // 7
	private String referringDoctor; // 8
	// private String consultingDoctor; // 9
	// private String admittingDoctor; // 17
	// UNKNOWN="U", ADULT="A",PEDIATRIC="P",NEONATE="N"
	private String patientType; // 18
	private String visitNumber; // 19 挂号号码
	private String customAttr1; // 42.1
	private String customName1; // 42.2
	private String customAttr2; // 43.1
	private String customName2; // 43.2
	private String admitDate; // 44
	private String customAttr3; // 50.1
	private String customName3; // 50.2
	private String customAttr4; // 52.1
	private String customName4; // 52.2

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getBed() {
		return bed;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}
	/**
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}
	/**
	 * @param macAddress the macAddress to set
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getVisitNumber() {
		return visitNumber;
	}

	public void setVisitNumber(String visitNumber) {
		this.visitNumber = visitNumber;
	}

	public String getAttendingDoctor() {
		return attendingDoctor;
	}

	public void setAttendingDoctor(String attendingDoctor) {
		this.attendingDoctor = attendingDoctor;
	}

	public String getReferringDoctor() {
		return referringDoctor;
	}

	public void setReferringDoctor(String referringDoctor) {
		this.referringDoctor = referringDoctor;
	}
//
//	public String getConsultingDoctor() {
//		return consultingDoctor;
//	}
//
//	public void setConsultingDoctor(String consultingDoctor) {
//		this.consultingDoctor = consultingDoctor;
//	}
//
//	public String getAdmittingDoctor() {
//		return admittingDoctor;
//	}
//
//	public void setAdmittingDoctor(String admittingDoctor) {
//		this.admittingDoctor = admittingDoctor;
//	}

	public String getPatientType() {
		return patientType;
	}

	// UNKNOWN="U", ADULT="A",PEDIATRIC="P",NEONATE="N"
	public void setPatientType(String patientType) {

		// egateway发送的result消息中不携带病人类型字段，
		if (patientType != null && (patientType.equalsIgnoreCase("U") || patientType.equalsIgnoreCase("A")
				|| patientType.equalsIgnoreCase("P") || patientType.equalsIgnoreCase("N"))) {
			this.patientType = patientType;
		} else {
			this.patientType = "";
		}
	}

	public String getCustomAttr1() {
		return customAttr1;
	}

	public void setCustomAttr1(String customAttr1) {
		this.customAttr1 = customAttr1;
	}

	public String getCustomName1() {
		return customName1;
	}

	public void setCustomName1(String customName1) {
		this.customName1 = customName1;
	}

	public String getCustomAttr2() {
		return customAttr2;
	}

	public void setCustomAttr2(String customAttr2) {
		this.customAttr2 = customAttr2;
	}

	public String getCustomName2() {
		return customName2;
	}

	public void setCustomName2(String customName2) {
		this.customName2 = customName2;
	}

	public String getAdmitDate() {
		return admitDate;
	}

	public void setAdmitDate(String admitDate) {
		this.admitDate = admitDate;
	}

	public String getCustomAttr3() {
		return customAttr3;
	}

	public void setCustomAttr3(String customAttr3) {
		this.customAttr3 = customAttr3;
	}

	public String getCustomName3() {
		return customName3;
	}

	public void setCustomName3(String customName3) {
		this.customName3 = customName3;
	}

	public String getCustomAttr4() {
		return customAttr4;
	}

	public void setCustomAttr4(String customAttr4) {
		this.customAttr4 = customAttr4;
	}

	public String getCustomName4() {
		return customName4;
	}

	public void setCustomName4(String customName4) {
		this.customName4 = customName4;
	}

}
