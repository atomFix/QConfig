package com.qconfig.common;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/29/11:18
 */
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = QConfigCommentConfig.class)
public class QConfigCommentConfig {
}
