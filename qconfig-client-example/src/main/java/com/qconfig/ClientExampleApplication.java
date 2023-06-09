package com.qconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Hello world!
 */
@SpringBootApplication
@ImportResource(value = {"classpath:spring.xml", "classpath:qconfig.xml"})
public class ClientExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientExampleApplication.class, args);
    }
}
