package com.qc.common.config;

import com.google.common.base.Strings;
import com.qc.common.thread.QConfigThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.StandardContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/29/11:11
 */
public abstract class RefreshableConfig {

    @Value("${qconfig.server.refresh_interval:60}")
    private int configRefreshInterval;
    private static final Logger logger = LoggerFactory.getLogger(RefreshableConfig.class);

    protected abstract List<RefreshablePropertySource > getRefreshablePropertySources();

    @Autowired
    private ConfigurableEnvironment environment;

    @PostConstruct
    public void setup() {
        List<RefreshablePropertySource> sources = getRefreshablePropertySources();
        if (CollectionUtils.isEmpty(sources)) {
            throw new IllegalArgumentException("sources is empty");
        }
        for (RefreshablePropertySource source : sources) {
            source.refresh();
            environment.getPropertySources().addLast(source);
        }

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, QConfigThreadFactory.create("refresh-config", true));

        executorService.scheduleWithFixedDelay(() -> {
            try {
                sources.forEach(RefreshablePropertySource::refresh);
            } catch (Exception e) {
                logger.error("refresh config error", e);
            }
        },  configRefreshInterval, configRefreshInterval, TimeUnit.SECONDS);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        String value = getValue(key);
        return value == null  ? defaultValue : Boolean.parseBoolean(value);
    }

    public String getValue(String key) {
        return environment.getProperty(key);
    }

}
