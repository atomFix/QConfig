package com.qconfig.client;

import com.qconfig.client.support.ConfigManager;
import com.qconfig.client.util.QConfigInjector;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/10:04
 */
public class ConfigServers {

    private ConfigServers() {}

    private static final ConfigServers instance = new ConfigServers();

    private volatile ConfigManager configManager;

    public ConfigManager getManager() {
        if (configManager == null) {
            synchronized (this) {
                if (configManager == null) {
                    configManager = QConfigInjector.getInstance(ConfigManager.class);
                }
            }
        }
        return configManager;
    }

    public static Config getConfig(String namespace) {
        return instance.getManager().getConfig(namespace);
    }
}
