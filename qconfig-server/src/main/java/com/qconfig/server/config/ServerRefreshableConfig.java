package com.qconfig.server.config;

import com.google.common.base.Splitter;
import com.qconfig.common.config.RefreshableConfig;
import com.qconfig.common.config.RefreshablePropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/30/08:39
 */
@Component
public class ServerRefreshableConfig extends RefreshableConfig {

    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    private final RefreshablePropertySource refreshablePropertySource;

    public ServerRefreshableConfig(final RefreshablePropertySource refreshablePropertySource) {
        this.refreshablePropertySource = refreshablePropertySource;
    }

    @Override
    protected List<RefreshablePropertySource> getRefreshablePropertySources() {
        return Collections.singletonList(refreshablePropertySource);
    }

    public List<String> getEurekaServerList() {
        final String eurekaServerList = getValue("eureka.service.url");
        if (eurekaServerList == null) {
            return Collections.emptyList();
        }
        List<String> result = SPLITTER.splitToList(eurekaServerList);
        return CollectionUtils.isEmpty(result) ? Collections.emptyList() : result;
    }
}
