package com.qconfig.client.support;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.common.util.concurrent.RateLimiter;
import com.qconfig.client.config.ConfigServerAddressLocator;
import com.qconfig.client.enums.ConfigSourceType;
import com.qconfig.client.util.QConfigInjector;
import com.qconfig.common.dto.QConfig;
import com.qconfig.common.entity.ServiceDTO;
import com.qconfig.common.http.HttpClient;
import com.qconfig.common.http.HttpRequest;
import com.qconfig.common.http.HttpResponse;
import com.qconfig.common.thread.QConfigThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/15:31
 */
@Slf4j
public class RemoteConfigRepository extends AbstractConfigRepository {

    private final static ScheduledExecutorService executorService;

    static {
        executorService = Executors.newScheduledThreadPool(1, QConfigThreadFactory.create("RemoteConfigRepository", true));
    }

    private final ConfigServerAddressLocator serverAddressLocator = QConfigInjector.getInstance(ConfigServerAddressLocator.class);
    private final AtomicReference<ServiceDTO> longPollServiceDto;
    private final Escaper escaper;
    private final HttpClient httpClient;
    private final AtomicReference<QConfig> configCache;
    private final String namespace;

    private final Joiner.MapJoiner joiner;

    private RateLimiter rateLimiter;


    public RemoteConfigRepository(String namespace) {
        this.configCache = new AtomicReference<>();
        this.namespace = namespace;
        this.longPollServiceDto = new AtomicReference<>();
        this.escaper = UrlEscapers.urlFormParameterEscaper();
        this.httpClient = QConfigInjector.getInstance(HttpClient.class);
        this.rateLimiter = RateLimiter.create(2);
        this.joiner = Joiner.on("&").withKeyValueSeparator("=");

        this.trySync();
        this.scheduleFetchConfig();
    }

    private void scheduleFetchConfig() {
        executorService.scheduleAtFixedRate(this::trySync, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public Properties getConfig() {
        if (configCache == null) {
            this.sync();
        }
        Properties properties = propertiesFactory.getPropertiesInstance();
        properties.putAll(configCache.get().getConfigurations());
        return properties;
    }

    @Override
    public ConfigSourceType getSourceType() {
        return ConfigSourceType.REMOTE;
    }

    @Override
    protected synchronized void sync() {
        QConfig previous = configCache.get();
        QConfig current = loadQConfig();

        if (previous != current) {
            configCache.set(current);
            this.fireRepositoryChange(current.getNamespace(), this.getConfig());
        }
    }

    private QConfig loadQConfig() {
        if (!rateLimiter.tryAcquire()) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {

            }
        }

        List<ServiceDTO> services = serverAddressLocator.getServices();
        Collections.shuffle(services);
        if (longPollServiceDto.get() != null) {
            services.add(0, longPollServiceDto.getAndSet(null));
        }

        try {
            for (ServiceDTO service : services) {
                String url = assembleQueryConfigUrl(service.getHomepageUrl(), "test", "dev", namespace, configCache.get());
                log.info("get config url is : {}", url);

                HttpResponse<QConfig> response = httpClient.doGet(new HttpRequest(url), QConfig.class);
                log.info("get status is : {}", response.getStatusCode());
                if (response.getStatusCode() == 304) {
                    return configCache.get();
                }
                log.info("getBody: {}", response.getBody());
                return response.getBody();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("error");
    }

    private String assembleQueryConfigUrl(String uri, String appId, String env, String namespace, QConfig config) {
        if (Strings.isNullOrEmpty(uri)) {
            throw new RuntimeException("get config uri is null!");
        }

        Map<String, String> requestParam = Maps.newHashMap();
        requestParam.put("appId", escaper.escape(appId));
        requestParam.put("namespace", escaper.escape(namespace));
        if (config != null && Objects.nonNull(config.getReleaseKey())) {
            requestParam.put("releaseKey", String.valueOf(config.getReleaseKey()));
        }
        String param = joiner.join(requestParam);

        return uri + "/config?" + param;
    }

}
