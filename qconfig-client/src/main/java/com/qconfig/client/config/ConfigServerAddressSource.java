package com.qconfig.client.config;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.qconfig.common.entity.ServiceDTO;
import com.qconfig.common.thread.QConfigThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/03/11:41
 */
@Component
@ConditionalOnProperty(name = "qconfig.client.enable1", havingValue = "true", matchIfMissing = false)
public class ConfigServerAddressSource {

    @Autowired
    private RestTemplate restTemplate;

    private final Type SERVICE_TYPE = new TypeToken<List<ServiceDTO>>() {
    }.getType();

    private final Gson GSON = new Gson();

    private static final Set<String> SERVICES_INFO = Sets.newHashSet();

    @PostConstruct
    public void refreshConfigServerUrls() {
        getServiceUrlAndMerge();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, QConfigThreadFactory.create("client-fetch-service", true));

        executorService.scheduleWithFixedDelay(this::getServiceUrlAndMerge, 30, 30, TimeUnit.SECONDS);
    }

    private void getServiceUrlAndMerge() {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:8080/config/refresh", String.class);
        entity.getBody();

        List<ServiceDTO> serviceList = GSON.fromJson(entity.getBody(), SERVICE_TYPE);
        if (CollectionUtils.isEmpty(serviceList)) {
            return;
        }
        serviceList.stream().map(ServiceDTO::getHomepageUrl).forEach(SERVICES_INFO::add);
    }

    public static Set<String> getServicesInfo() {
        return SERVICES_INFO;
    }

}
