package com.mindray.webservice.adt;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://egateway.mindray.com",
        name = "Manage",
        serviceName = "manageAdt",
        endpointInterface = "com.mindray.webservice.adt.Manage")
@SOAPBinding()
public interface Manage {
    @WebMethod()
    @RequestWrapper(localName = "manageAdt", targetNamespace = "http://egateway.mindray.com", className = "com.mindray.webservice.adt.xml.ManageAdt")
    @ResponseWrapper(localName = "manageAdtResponse", targetNamespace = "http://egateway.mindray.com", className = "com.mindray.webservice.adt.xml.ManageAdtResponse")
    @WebResult(name = "manageAdtReturn", targetNamespace = "http://egateway.mindray.com")
    String manageAdt(@WebParam(name = "callType") String callType,@WebParam(name = "message") String message);
}
