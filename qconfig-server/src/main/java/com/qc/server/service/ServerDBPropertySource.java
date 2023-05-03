package com.qc.server.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.qc.biz.entity.ServerConfig;
import com.qc.biz.repository.ServerConfigRepository;
import com.qc.common.config.RefreshablePropertySource;
import com.qc.common.entity.ConfigContent;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/30/09:13
 */
@Component
@Slf4j
public class ServerDBPropertySource extends RefreshablePropertySource {

    public final  ServerConfigRepository serverConfigRepository;

    public ServerDBPropertySource(final String name, Map<String, Object> source, ServerConfigRepository serverConfigRepository) {
        super(name, source);
        this.serverConfigRepository = serverConfigRepository;
    }

    @Autowired
    public ServerDBPropertySource(final ServerConfigRepository serverConfigRepository) {
        super("DBConfig", Maps.newConcurrentMap());
        this.serverConfigRepository = serverConfigRepository;
    }

    @Override
    public void refresh() {
        List<ServerConfig> configs = serverConfigRepository.findAll();

        Map<String, Object> dbMap = Maps.newHashMap();
        for (ServerConfig config : configs) {
            if (Objects.equals(config.getCluster(), ConfigContent.QCONFIG_CLUSTER)) {
                dbMap.put(config.getKey(), config.getValue());
            }
        }

        if (!Strings.isNullOrEmpty(System.getProperty(ConfigContent.QCONFIG_CLUSTER))) {
            String cluster = System.getProperty(ConfigContent.QCONFIG_CLUSTER);
            for (ServerConfig config : configs) {
                if (Objects.equals(cluster, config.getCluster())) {
                    dbMap.put(config.getKey(), config.getValue());
                }
            }
        }

        for (Map.Entry<String, Object> entry : dbMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (this.source.get(key) == null) {
                log.info("Load config from DB : {} = {}", key, value);
            } else if (!Objects.equals(this.source.get(key), value)) {
                log.info("Load config from DB : {} = {}. Old value = {}", key,
                        value, this.source.get(key));
            }
            source.put(key, value);
        }

    }
}
