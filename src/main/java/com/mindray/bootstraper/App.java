package com.mindray.bootstraper;

import com.mindray.egateway.PropertiesUtil;
import com.mindray.egateway.hl7.DataConfig;
import com.mindray.egateway.hl7.HL7Client;
import com.mindray.egateway.hl7.HL7Util;
import joptsimple.internal.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;

import com.mindray.egateway.hl7.HL7Server;

@Component
public class App implements ApplicationRunner, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(App.class);
	@Resource
	private HL7Client hl7_adt_client;

	private HL7Server sHl7server;

	private ApplicationContext context;

	public void stop() {
		try {
			if (hl7_adt_client != null) {
				hl7_adt_client.disconnect();
			}
			if(sHl7server!=null){
				sHl7server.stop();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void start() {
		try {
			DataConfig.getIns().LoadDataConfig("DataConfig.xml");
			PropertiesUtil propUtil = PropertiesUtil.getIns();
			propUtil.init();

			//eGateway Adt监听模式
			String eGatewayIp = propUtil.getProperty("eGatewayIp");
			int adtListenPort = Integer.parseInt(propUtil.getProperty("adtListenPort"));
			if(!Strings.isNullOrEmpty(eGatewayIp)){
				logger.info("[mindray-info]eGatewayIp:{},adtListenPort:{}",eGatewayIp,adtListenPort);
				hl7_adt_client.connect(eGatewayIp, adtListenPort);
			}else{
				logger.info("[mindray-info]egatewayIp is empty,disable connect eagateway.");
			}
			HL7Util.getIns();
			// eGateway 主动客户端方式，Adt 查询模式
			int port = Integer.parseInt(propUtil.getProperty("port"));
			String charset = propUtil.getProperty("charset");
			sHl7server = new HL7Server(port, charset);
			sHl7server.start();

			//开启workers,从eGateway推送到本中间件的消息，都是通过该类进行消费
			//该类废弃，直接使用hubWorker类进行线程直接处理
			//queryProcessor.start();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	@Override
	public void run(ApplicationArguments args) throws Exception {
		 this.start();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
