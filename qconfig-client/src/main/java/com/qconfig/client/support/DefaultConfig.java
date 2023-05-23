package com.qconfig.client.support;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qconfig.client.enums.ConfigSourceType;
import com.qconfig.client.model.ConfigChange;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/12:41
 */
@Slf4j
public class DefaultConfig extends AbstractConfig implements RepositoryChangeListener {

    private final String namespace;
    private final AtomicReference<Properties> configProperties;
    private final ConfigRepository configRepository;
    private final Properties resourceProperties;

    private volatile ConfigSourceType configSourceType = ConfigSourceType.NONE;


    public DefaultConfig(String namespace, ConfigRepository configRepository) {
        this.namespace = namespace;
        this.configRepository = configRepository;
        this.configProperties = new AtomicReference<>();
        this.resourceProperties = loadFromResource(namespace);
        initialize();
    }

    private void initialize() {
        try {
            updateConfig(configRepository.getConfig(), configRepository.getSourceType());
        } catch (Throwable throwable) {
            log.error("DefaultConfig initialize", throwable);
        } finally {
            configRepository.addChangeListener(this);
        }
    }

    private void updateConfig(Properties config, ConfigSourceType configSourceType) {
        configProperties.set(config);
        this.configSourceType = configSourceType;
    }

    @Override
    public Set<String> getPropertyNames() {
        Set<String> repository = this.getPropertyNamesFromRepository();
        Set<String> additional = this.getPropertyNamesFromAdditional();
        if (repository == null || repository.isEmpty()) {
            return additional;
        }
        if (additional == null || additional.isEmpty()) {
            return repository;
        }
        Set<String> propertiesName = Sets.newLinkedHashSetWithExpectedSize(repository.size() + additional.size());
        propertiesName.addAll(repository);
        propertiesName.addAll(additional);
        return propertiesName;
    }

    private Set<String> getPropertyNamesFromAdditional() {
        Properties properties = configProperties.get();
        if (properties == null) {
            return Collections.emptySet();
        }
        return this.stringPropertyNames(properties);
    }

    private Set<String> getPropertyNamesFromRepository() {
        Properties properties = resourceProperties;
        if (properties == null) {
            return Collections.emptySet();
        }
        return this.stringPropertyNames(properties);
    }

    private Set<String> stringPropertyNames(Properties properties) {
        Map<String, String> map = Maps.newLinkedHashMapWithExpectedSize(properties.size());
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String && value instanceof String) {
                map.put((String) key, (String) value);
            }
        }
        return map.keySet();
    }

    @Override
    public String  getProperty(String name, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null) {
            value = getPropertyFromRepository(name);
        }

        if (value == null) {
            value = System.getenv(name);
        }

        if (value == null) {
            value = getPropertyFromAdditional(name);
        }
        return value;
    }

    @Override
    public ConfigSourceType getSourceType() {
        return configSourceType;
    }

    private String getPropertyFromAdditional(String name) {
        Properties properties = this.resourceProperties;
        if (properties != null) {
            return properties.getProperty(name);
        }
        return null;
    }

    private String getPropertyFromRepository(String name) {
        Properties properties = configProperties.get();
        if (properties != null) {
            return properties.getProperty(name);
        }
        return null;
    }


    @Override
    public void onRepositoryChange(String namespace, Properties newProperties) {
        if (newProperties.equals(configProperties.get())) {
            return;
        }

        Properties propertiesInstance = propertiesFactory.getPropertiesInstance();
        propertiesInstance.putAll(newProperties);

        Map<String, ConfigChange> changeMap = updateAndCalcConfigChange(newProperties, configSourceType);
        this.publishConfigChange(namespace, changeMap);
    }

    private Map<String, ConfigChange> updateAndCalcConfigChange(Properties newProperties, ConfigSourceType configSourceType) {
        List<ConfigChange> configChanges = calcPropertyChanges(namespace, configProperties.get(), newProperties);

        return null;
    }

    private Properties loadFromResource(String namespace) {
        String name = String.format("META-INF/config/%s.properties", namespace);
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        Properties properties = null;
        if (inputStream != null) {
            properties = propertiesFactory.getPropertiesInstance();
            try {
                properties.load(inputStream);
            } catch (IOException exception) {

            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
        return properties;
    }
}
