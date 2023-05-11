package com.qconfig.server;

import com.qconfig.biz.QConfigBizConfig;
import com.qconfig.common.QConfigCommentConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
