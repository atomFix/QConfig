package com.qc.server;

import com.qc.biz.QConfigBizConfig;
import com.qc.common.QConfigCommentConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackageClasses = {QConfigServer.class, QConfigCommentConfig.class, QConfigBizConfig.class})
@EnableTransactionManagement
public class QConfigServer
{

    /**
     * 后台服务器
     * @param args 参数
     */
    public static void main( String[] args )
    {
        SpringApplication.run(QConfigServer.class, args);
    }
}
