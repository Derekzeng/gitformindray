/**
 * 
 */
package com.mindray.egateway.model;

import com.google.gson.*;

/**
 * @author 50220398
 *
 */
public class PatientResult {
	private Msh msh;
	private Pid pid;
	private Pv1 pv1;
	private ParameterResult paraResult;
	private AlarmResult alarmResult;
	private WaveformResult waveformResult;

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

	public Msh getMsh() {
		return msh;
	}

	public void setMsh(Msh msh) {
		this.msh = msh;
	}

	public ParameterResult getParameterResult() {
		return paraResult;
	}

	public void setParameterResul(ParameterResult result) {
		paraResult = result;
	}

	public WaveformResult getWaveformResult() {
		return waveformResult;
	}

	public void setWaveformResul(WaveformResult result) {
		waveformResult = result;
	}

	public AlarmResult getAlarmResult() {
		return alarmResult;
	}

	public void setAlarmResult(AlarmResult result) {
		alarmResult = result;
	}

	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
