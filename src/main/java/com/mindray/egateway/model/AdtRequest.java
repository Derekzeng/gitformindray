/**
 * 
 */
package com.mindray.egateway.model;

import com.google.gson.Gson;

/**
 * @author 50220398 The request is the query key from the QPD segment of the adt
 *         query message.
 *
 */
public class AdtRequest {
	private String pid;
	private String vid;
	private String firstName;
	private String lastName;
	private String room;
	private String bed;
	private String facility;
	private String department;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
