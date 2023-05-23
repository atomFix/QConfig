package com.qconfig.client.support;

import com.google.common.collect.Maps;
import com.qconfig.client.util.QConfigInjector;

import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/10:19
 */
public class DefaultConfigFactoryManager implements ConfigFactoryManager{

    private Map<String, ConfigFactory> factoryCache = Maps.newConcurrentMap();

    @Override
    public ConfigFactory getFactory(String namespace) {
        ConfigFactory configFactory = factoryCache.get(namespace);
        if (configFactory != null) {
            return configFactory;
        }

        configFactory = QConfigInjector.getInstance(ConfigFactory.class);
        factoryCache.put(namespace, configFactory);
        return configFactory;
    }
}
