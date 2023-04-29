package com.qc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class QConfigServer
{

    /**
     * 递归调用
     * @param args
     */


    public static void main( String[] args )
    {
        SpringApplication.run(QConfigServer.class, args);
    }
}
