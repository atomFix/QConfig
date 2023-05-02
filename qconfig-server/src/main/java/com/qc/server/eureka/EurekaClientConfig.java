package com.qc.server.eureka;

import com.qc.server.config.ServerRefreshableConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/01/16:43
 */
@Component
@Primary
@ConditionalOnProperty(value = "qconfig.server.enable", havingValue = "true", matchIfMissing = false)
public class EurekaClientConfig extends EurekaClientConfigBean {


    private final RefreshScope refreshScope;

    private final ServerRefreshableConfig serverRefreshableConfig;

    private static final String EUREKA_CLIENT  = "eurekaClient";

    public EurekaClientConfig(final RefreshScope refreshScope, final ServerRefreshableConfig refreshableConfig) {
        this.refreshScope = refreshScope;
        this.serverRefreshableConfig = refreshableConfig;
    }

    @EventListener
    public void refreshClientConfig(ApplicationReadyEvent event) {
        if (!isFetchRegistry()) {
            setFetchRegistry(true);
            setRegisterWithEureka(true);
            refreshScope.refresh(EUREKA_CLIENT);
        }
    }

    @Override
    public List<String> getEurekaServerServiceUrls(String myZone) {
        List<String> serverList = serverRefreshableConfig.getEurekaServerList();
        return CollectionUtils.isEmpty(serverList) ? super.getEurekaServerServiceUrls(myZone) : serverList;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
