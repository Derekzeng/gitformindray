package com.mindray.webservice.adt.xml;

import javax.xml.bind.annotation.*;
import java.io.Serializable;


@XmlAccessorType(XmlAccessType.FIELD)
public class ManageAdt implements Serializable {

	@XmlElement(required = true)
	protected String callType;
	@XmlElement(required = true)
	protected String message;

	public String getCallType() {
		return callType;
	}
	public void setCallType(String callType) {
		this.callType = callType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
