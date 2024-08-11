package com.mindray.config;

import com.mindray.cis.provider.HttpClientProvider;
import com.mindray.cis.provider.WebServiceProvider;
import com.mindray.egateway.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

public class PushProviderImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(WebServiceConfig.class);
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String methondType = PropertiesUtil.getIns().getProperty("methondType");
        logger.info("[info] 根据配置，推送给第三方的方式为{}", methondType);
        if(methondType.equalsIgnoreCase("http")){
            BeanDefinitionBuilder beanDef1 = BeanDefinitionBuilder.rootBeanDefinition(HttpClientProvider.class);
            registry.registerBeanDefinition("iPushProvider", beanDef1.getBeanDefinition());
        }
        if(methondType.equalsIgnoreCase("webservice")){
            BeanDefinitionBuilder beanDef2 = BeanDefinitionBuilder.rootBeanDefinition(WebServiceProvider.class);
            registry.registerBeanDefinition("iPushProvider", beanDef2.getBeanDefinition());
        }
    }

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
