package com.qconfig.client.config;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.common.reflect.TypeToken;
import com.qconfig.client.util.QConfigInjector;
import com.qconfig.common.http.HttpClient;
import com.qconfig.common.http.HttpRequest;
import com.qconfig.common.http.HttpResponse;
import com.qconfig.common.entity.ServiceDTO;
import com.qconfig.common.thread.QConfigThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/03/11:41
 */
@Slf4j
public class ConfigServerAddressLocator {

    private final Type SERVICE_TYPE;
    private final AtomicReference<List<ServiceDTO>> configServices;
    private HttpClient httpClient;

    private static final Joiner.MapJoiner MAP_JOINER = Joiner.on("&").withKeyValueSeparator("=");

    private static final Escaper ESCAPER = UrlEscapers.urlFormParameterEscaper();


    public ConfigServerAddressLocator() {
        this.configServices = new AtomicReference<>(Lists.newArrayList());
        this.httpClient = QConfigInjector.getInstance(HttpClient.class);
        this.SERVICE_TYPE = new TypeToken<List<ServiceDTO>>() {
        }.getType();
        log.info("QCONFIG : updateServiceUrlAndMerge execute !");

        initializeAutoServerInfos();
    }

    public void initializeAutoServerInfos() {
        updateServiceUrlAndMerge();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, QConfigThreadFactory.create("client-fetch-service", true));
        executorService.scheduleWithFixedDelay(this::updateServiceUrlAndMerge, 30, 30, TimeUnit.SECONDS);
    }

    private synchronized void updateServiceUrlAndMerge() {
        HttpResponse<List<ServiceDTO>> entity = null;
        try {
            entity = httpClient.doGet(new HttpRequest(assembleMetaServiceUrl()), SERVICE_TYPE);
            log.info("get services local is : {}", entity.getBody());
        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.error("has an error !", e);
            return;
        }

        List<ServiceDTO> serviceList = entity.getBody();
        if (CollectionUtils.isEmpty(serviceList)) {
            return;
        }
        configServices.set(serviceList);
    }


    public List<ServiceDTO> getServices() {
        if (CollectionUtils.isEmpty(configServices.get())) {
            updateServiceUrlAndMerge();
        }
        return configServices.get();
    }

    public String assembleMetaServiceUrl() {
        String uri = "http://localhost:8083";
        Map<String, String> requestParam = Maps.newHashMap();
        requestParam.put("appId", ESCAPER.escape("test"));
        return uri + "/config/refresh?" + MAP_JOINER.join(requestParam);
    }
}
