package com.mindray.egateway.model;
import com.google.gson.Gson;

public class MdmResult {
	private Msh msh;
	private Pid pid;
	private Pv1 pv1;
	private String path;

	/**
	 * @return the msh
	 */
	public Msh getMsh() {
		return msh;
	}

	/**
	 * @param msh the msh to set
	 */
	public void setMsh(Msh msh) {
		this.msh = msh;
	}

	/**
	 * @return the pid
	 */
	public Pid getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(Pid pid) {
		this.pid = pid;
	}

	/**
	 * @return the pv1
	 */
	public Pv1 getPv1() {
		return pv1;
	}

	/**
	 * @param pv1 the pv1 to set
	 */
	public void setPv1(Pv1 pv1) {
		this.pv1 = pv1;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
