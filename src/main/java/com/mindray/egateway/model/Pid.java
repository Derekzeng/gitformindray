/**
 * 
 */
package com.mindray.egateway.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 50220398
 *
 */

public class Pid {

	private String pid; // 3.1
	private String lastName; // 5.1.1
	private String firstName; // 5.2
	private String middleName; // 5.3
	private String birthday; // 7
	private String genderCode; // 8
	private String race; // 10
	private String address; // 11.1
	private String postalCode;// 11.5
	private String homePhone; // 13
	private String workPhone; // 14
	private String ssnNumber; // social sn, 19
	private String customAttr1; // 35.1
	private String customName1; // 35.2
	private String customAttr2; // 36.1
	private String customName2; // 36.2
	private String customAttr3; // 38.1
	private String customName3; // 38.2
	private String customAttr4; // 39.1
	private String customName4; // 39.2

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGenderCode() {
		return genderCode;
	}

	// <Blank> 未定义
	// M 男性
	// F 女性
	// U 未知
	// 发给egateway的Gender需要是以上的3种,否则设置为"",如："M"
	public void setGenderCode(String genderCode) {
		if (!StringUtils.isEmpty(genderCode) && (genderCode.equalsIgnoreCase("M") || genderCode.equalsIgnoreCase("F")
				|| genderCode.equalsIgnoreCase("U"))) {
			this.genderCode = genderCode;
		} else {
			this.genderCode = "";
		}
	}

	public String getRace() {
		return race;
	}

	// 1002-5 American Indian or Alaska Native
	// 2028-9 Asian
	// 2054-5 Black or African American
	// 2076-8 Native Hawaiian or Other Pacific Islander
	// 2106-3 White
	// 2131-1 Other Race
	// 发给egateway的race需要是以上的6种,否则设置为"",如："1002-5"
	public void setRace(String race) {
		if (!StringUtils.isEmpty(race) && (race.equalsIgnoreCase("1002-5") || race.equalsIgnoreCase("2028-9")
				|| race.equalsIgnoreCase("2054-5") || race.equalsIgnoreCase("2076-8") || race.equalsIgnoreCase("2106-3")
				|| race.equalsIgnoreCase("2131-1"))) {

			this.race = race;
		} else {
			this.race = "";
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getCustumAttr1() {
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

	public String getSsnNumber() {
		return ssnNumber;
	}

	public void setSsnNumber(String ssnNumber) {
		this.ssnNumber = ssnNumber;
	}

}
