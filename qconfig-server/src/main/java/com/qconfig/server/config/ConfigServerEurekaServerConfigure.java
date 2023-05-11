package com.qconfig.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/02/17:48
 */
@Configuration
@EnableEurekaServer
@ConditionalOnProperty(name = "qconfig.server.eureka.enabled", havingValue = "true", matchIfMissing = true)
@EnableDiscoveryClient
public class ConfigServerEurekaServerConfigure {


}
