package com.qconfig.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/16/00:16
 */
@Data
@RequiredArgsConstructor
public class ConfigChangeEvent {

    @NonNull
    private final String namespace;

    @NonNull
    private final Map<String, ConfigChange> configMaps;

    public Set<String> getConfigKey() {
        return configMaps.keySet();
    }

}
