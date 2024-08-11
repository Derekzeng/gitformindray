/**
 * 
 */
package com.mindray.egateway.model;

/**
 * @author 50220398
 *
 */
public class Msh {
	private String msgDateTime; // 7
	private String msgTypeCode; // 9.1
	private String msgTypeTriggerEvent; // 9.2
	private String msgControlId; // 10
	private String msgSeqNumber; // 13

	public String getMsgDateTime() {
		return msgDateTime;
	}

	public void setMsgDateTime(String msgDateTime) {
		this.msgDateTime = msgDateTime;
	}

	public String getMsgTypeCode() {
		return msgTypeCode;
	}

	public void setMsgTypeCode(String msgTypeCode) {
		this.msgTypeCode = msgTypeCode;
	}

	public String getMsgTypeTriggerEvent() {
		return msgTypeTriggerEvent;
	}

	public void setMsgTypeTriggerEvent(String msgTypeTriggerEvent) {
		this.msgTypeTriggerEvent = msgTypeTriggerEvent;
	}

	public String getMsgControlId() {
		return msgControlId;
	}

	public void setMsgControlId(String msgControlId) {
		this.msgControlId = msgControlId;
	}

	public String getMsgSeqNumber() {
		return msgSeqNumber;
	}

	public void setMsgSeqNumber(String msgSeqNumber) {
		this.msgSeqNumber = msgSeqNumber;
	}

}
