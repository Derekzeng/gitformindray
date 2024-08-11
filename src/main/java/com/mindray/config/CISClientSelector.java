package com.mindray.config;

import com.mindray.config.annatation.EnableAutoCISClientConfiguration;
import com.mindray.config.util.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CISClientSelector implements ImportSelector {
    Logger logger = LoggerFactory.getLogger(CISClientSelector.class);
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata annotationMetadata) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableAutoCISClientConfiguration.class.getName());
        if(attributes==null){
            return new String[0];
        }
        String[] clazz = (String[]) attributes.get("value");
        if(clazz==null){
            return new String[0];
        }
        List<String> allClassWithAnnoEnableCISClient = ClassUtils.getAllClassWithAnnoEnableCISClient(clazz);
        ArrayList<String> importCls = new ArrayList<>();
        allClassWithAnnoEnableCISClient.forEach(c -> importCls.add(c));
        return importCls.toArray(new String[0]);
    }
}
