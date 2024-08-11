package com.mindray.config.annatation;

import com.mindray.config.CISClientSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CISClientSelector.class)
public @interface EnableAutoCISClientConfiguration {
    String[] value() default {};
}
