package com.qconfig.client.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;


/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/11/10:47
 */
@ConditionalOnProperty(value = "qconfig.client.enable", havingValue = "true", matchIfMissing = true)
@Configuration
public class QConfigRegisterConfiger {


}
