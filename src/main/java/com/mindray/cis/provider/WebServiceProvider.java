package com.mindray.cis.provider;

import com.mindray.egateway.PropertiesUtil;
import joptsimple.internal.Strings;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class WebServiceProvider implements IPushProvider {
    private static final Logger logger = LoggerFactory.getLogger(WebServiceProvider.class);
    private String wsdl;
    private String method;
    public String SendByWs(Object... objs){
        if(Strings.isNullOrEmpty(this.wsdl)){
            logger.info("{} sendByWs wsdl may not be empty.",this.getClass().getName());
            return "";
        }
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsdl);
		try {
			Object[] objects = client.invoke(method,objs);
			if(objects.length>0){
			    return objects[0].toString();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
    }
    @PostConstruct
    public void init(){
        this.wsdl =PropertiesUtil.getIns().getProperty("datasaveurl");
        this.method =PropertiesUtil.getIns().getProperty("action");
    }
    @Override
    public String sendJson(String json) {
        return this.SendByWs(json);
    }
}
