package com.qconfig.biz;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/02/00:16
 */
@Configuration
@ComponentScan(basePackageClasses = QConfigBizConfig.class)
@EnableAutoConfiguration
public class QConfigBizConfig {
}
