package com.qconfig.server.discovery;

import com.google.gson.Gson;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.qconfig.common.entity.ServiceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/03/11:11
 */
@Service
public class DefaultDiscovery implements DiscoveryService {

    private Logger logger = LoggerFactory.getLogger(DefaultDiscovery.class);

    private Gson GSON = new Gson();

    final EurekaClient eurekaClient;

    public DefaultDiscovery(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    @Override
    public List<ServiceDTO> getServiceInstances(String serviceId) {
        Application application = eurekaClient.getApplication(serviceId);
        if (application == null) {
            logger.warn("serviceId: {} not found", serviceId);
            return Collections.emptyList();
        }
        List<ServiceDTO> result = application.getInstances().stream().map(INSTANCE_TO_SERVICE_DTO).collect(Collectors.toList());
        logger.info("serviceId: {} instances: {}", serviceId, GSON.toJson(result));
        return result;
    }

    private static final Function<InstanceInfo, ServiceDTO> INSTANCE_TO_SERVICE_DTO = instance -> {
        ServiceDTO serviceDto = new ServiceDTO();
        serviceDto.setInstanceId(instance.getInstanceId());
        serviceDto.setAppName(instance.getAppName());
        serviceDto.setHomepageUrl(instance.getHomePageUrl());
        return serviceDto;
    };
}
