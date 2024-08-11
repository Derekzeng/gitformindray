package com.mindray.config.annatation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCISClient {
    boolean isEnable() default true;
}
