package com.qc.server.config;

import com.qc.biz.entity.ServerConfig;
import com.qc.biz.repository.ServerConfigRepository;
import com.qc.common.entity.ConfigContent;
import com.qc.server.eureka.EurekaClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/02/17:48
 */
@Configuration
@EnableEurekaServer
@ConditionalOnProperty(name = "qconfig.server.eureka.enabled", havingValue = "true", matchIfMissing = true)
public class ConfigServerEurekaServerConfigure {


}
