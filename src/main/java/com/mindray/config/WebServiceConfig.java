package com.mindray.config;

import com.mindray.bootstraper.App;
import com.mindray.webservice.adt.Manage;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import javax.xml.ws.Endpoint;

@Configuration
@Import(PushProviderImportBeanDefinitionRegistrar.class)
public class WebServiceConfig {
    @Resource
    private Manage manage;
    @Resource
    private Bus bus;

    private static final Logger logger = LoggerFactory.getLogger(WebServiceConfig.class);
    @Bean
    public ServletRegistrationBean<CXFServlet> myServletBean(){
        return new ServletRegistrationBean<>(new CXFServlet(),"/services/*");
    }

    @Bean
    public Endpoint endPointManageService(){
        EndpointImpl endpoint = new EndpointImpl(bus, manage);
        //endpoint.publish("/adt");
        //logger.info("[info] Manage already publish,wsdl visit:/services/adt?wsdl");
       // return Endpoint.publish("/adt",manage);
        return endpoint;
    }
}
