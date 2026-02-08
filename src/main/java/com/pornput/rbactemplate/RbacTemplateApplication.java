package com.pornput.rbactemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RbacTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(RbacTemplateApplication.class, args);
    }

}
