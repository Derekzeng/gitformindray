package com.mindray;

import com.mindray.config.annatation.EnableAutoCISClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoCISClientConfiguration(value = {"com.mindray.cis.hospital"})
public class MindrayApplication {
    public static void main(String[] args) {

        //System.out.println("");
        SpringApplication.run(MindrayApplication.class,args);
    }
}
