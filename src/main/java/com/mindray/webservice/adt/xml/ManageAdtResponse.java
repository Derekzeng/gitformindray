
package com.mindray.webservice.adt.xml;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ManageAdtResponse {

    @XmlElement(required = true)
    protected String manageAdtReturn;
    public String getManageAdtReturn() {
        return manageAdtReturn;
    }
    public void setManageAdtReturn(String value) {
        this.manageAdtReturn = value;
    }

}
