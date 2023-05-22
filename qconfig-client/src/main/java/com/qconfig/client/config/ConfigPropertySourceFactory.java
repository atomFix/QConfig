package com.qconfig.client.config;

import com.google.common.collect.Lists;
import com.qconfig.client.Config;

import java.util.List;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/17:20
 */
public class ConfigPropertySourceFactory {

    private final List<ConfigPropertySource> configPropertySourceList = Lists.newLinkedList();

    public ConfigPropertySource create(String name, Config source) {
        ConfigPropertySource configPropertySource = new ConfigPropertySource(name, source);
        configPropertySourceList.add(configPropertySource);
        return configPropertySource;
    }

    public List<ConfigPropertySource> getAllConfigPropertySource() {
        return Lists.newLinkedList(configPropertySourceList);
    }

}
