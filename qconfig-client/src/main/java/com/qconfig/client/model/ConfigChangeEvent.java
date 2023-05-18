package com.qconfig.client.model;

import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/16/00:16
 */
@AllArgsConstructor
public class ConfigChangeEvent {

    private final String namespace;

    private final Map<String, ConfigChange> configMaps;

}
