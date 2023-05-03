package com.qc.server.config;

import com.github.rholder.retry.*;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.qc.biz.entity.ServerConfig;
import com.qc.biz.repository.ServerConfigRepository;
import com.qc.common.entity.ConfigContent;
import com.qc.server.eureka.EurekaClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/02/22:45
 */
@Configuration
@ConditionalOnProperty(name = "qconfig.server.auto_register", havingValue = "true", matchIfMissing = false)
public class EurekaServerDBAutoConfigure {

    private Logger logger = LoggerFactory.getLogger(EurekaServerDBAutoConfigure.class);

    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    @Autowired
    private ServerConfigRepository serverConfigRepository;

    @Autowired
    private EurekaClientConfig eurekaClientConfig;

    @Value("${eureka.client.service-url.defaultZone}")
    public String serviceUrl;

    @EventListener
    public void registerServerUrl(ApplicationReadyEvent event) {
        if (Strings.isNullOrEmpty(serviceUrl)) {
            logger.warn("eureka.client.service-url.defaultZone is null");
            return;
        }
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                //抛出runtime异常、checked异常时都会重试，但是抛出error不会重试。
                .retryIfException()
                //返回false也需要重试
                .retryIfResult(aBoolean -> Objects.equals(aBoolean, false))
                //重调策略
                .withWaitStrategy(WaitStrategies.fixedWait(10, TimeUnit.SECONDS))
                //尝试次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        try {
            Boolean result = retryer.call(() -> {
                // 这里放需要执行的代码
                try {
                    registerServerUrl();
                } catch (Exception e) {
                    return false;
                }
                return true;
            });
        } catch (RetryException | ExecutionException e) {
            // 处理异常
        }
    }

    private void registerServerUrl() {
        ServerConfig serverConfig = serverConfigRepository.findFirstByKeyAndCluster(ConfigContent.QCONFIG_SERVER_URL, ConfigContent.QCONFIG_CLUSTER);
        if (Objects.isNull(serverConfig)) {
            serverConfig = new ServerConfig();
            serverConfig.setKey(ConfigContent.QCONFIG_SERVER_URL);
            serverConfig.setCluster(ConfigContent.QCONFIG_CLUSTER);
            serverConfig.setValue(serviceUrl);
            serverConfig.setVersion(0);
            serverConfigRepository.save(serverConfig);
            return;
        }

        String configValue = serverConfig.getValue();
        Set<String> dbUrls = Sets.newHashSet(SPLITTER.splitToList(configValue));
        Set<String> localUrls = Sets.newHashSet(SPLITTER.splitToList(serviceUrl));

        List<String> differList = new ArrayList<>(Sets.difference(localUrls, dbUrls));
        if (CollectionUtils.isEmpty(differList)) {
            logger.info("registerServerUrl: {} is already registered", serviceUrl);
            return;
        }
        dbUrls.addAll(differList);
        String value = String.join(",", dbUrls);

        int updateResult = serverConfigRepository.updateValueByKeyAndVersion(serverConfig.getKey(), serverConfig.getVersion(), value);
        if (updateResult == 0) {
            throw new RuntimeException("registerServerUrl: update value failed");
        }
    }


    @PreDestroy
    public void destroy() {
        logger.info("destroy: {}", this.getClass().getSimpleName());
        if (Strings.isNullOrEmpty(serviceUrl)) {
            logger.warn("eureka.client.service-url.defaultZone is null");
            return;
        }
        ServerConfig serverConfig = serverConfigRepository.findFirstByKeyAndCluster(ConfigContent.QCONFIG_SERVER_URL, ConfigContent.QCONFIG_CLUSTER);
        String value = serverConfig.getValue();
        Set<String> dbUrls = Sets.newHashSet(SPLITTER.split(value));
        List<String> newDBUrls = new ArrayList<>(Sets.difference(dbUrls, Sets.newHashSet(SPLITTER.split(serviceUrl))));
        int updateResult = serverConfigRepository.updateValueByKeyAndVersion(serverConfig.getKey(), serverConfig.getVersion(), String.join(",", newDBUrls));
        if (updateResult == 0) {
            throw new RuntimeException("registerServerUrl: update value failed");
        }
    }
}
