package com.qc.server;

import com.qc.biz.QConfigBizConfig;
import com.qc.common.QConfigCommentConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {QConfigServer.class, QConfigCommentConfig.class, QConfigBizConfig.class})
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
