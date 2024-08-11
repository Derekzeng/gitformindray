package com.mindray.config.util;

import com.mindray.config.annatation.EnableCISClient;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassUtils {
    public static List<String> getAllClassWithAnnoEnableCISClient(String[] packageNames) {
        List<String> ret = new ArrayList<>();
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
        Arrays.stream(packageNames).forEach(packageName->{
            String pattern = PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils.convertClassNameToResourcePath(packageName)
                    +"/**/*.class";
            org.springframework.core.io.Resource[] resources = null;
            try {
                resources = pathMatchingResourcePatternResolver
                        .getResources(pattern);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (org.springframework.core.io.Resource resource : resources) {
                MetadataReader reader = null;
                Class<?> clazz = null;
                try {
                    reader = cachingMetadataReaderFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    clazz = Class.forName(className);
                    EnableCISClient annotation1 = clazz.getAnnotation(EnableCISClient.class);
                    if(annotation1.isEnable()) {
                        AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
                        ret.add(className);
                    }
                } catch (IOException|ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return ret;
    }
}
