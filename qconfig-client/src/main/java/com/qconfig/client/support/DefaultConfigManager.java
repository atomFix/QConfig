package com.qconfig.client.support;

import com.google.common.collect.Maps;
import com.qconfig.client.Config;
import com.qconfig.client.util.QConfigInjector;

import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/10:07
 */
public class DefaultConfigManager implements ConfigManager {

    private Map<String, Config> CONFIG_MAP = Maps.newConcurrentMap();

    private ConfigFactoryManager configFactoryManager;

    public DefaultConfigManager() {
        this.configFactoryManager = QConfigInjector.getInstance(ConfigFactoryManager.class);
    }

    @Override
    public Config getConfig(String namespace) {
        Config config = CONFIG_MAP.get(namespace);
        if (config != null) {
            return config;
        }

        synchronized (this) {
            config = configFactoryManager.getFactory(namespace).create(namespace);
            CONFIG_MAP.put(namespace, config);
        }

        return config;
    }
}
