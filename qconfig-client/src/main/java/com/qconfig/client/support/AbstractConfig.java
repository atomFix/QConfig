package com.qconfig.client.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qconfig.client.Config;
import com.qconfig.client.ConfigChangeListener;
import com.qconfig.client.model.ConfigChange;
import com.qconfig.client.model.ConfigChangeEvent;
import com.qconfig.client.util.QConfigInjector;
import com.qconfig.common.enums.PropertyChangeType;
import com.qconfig.common.thread.QConfigThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/16:55
 */
@Slf4j
public abstract class AbstractConfig implements Config {

    private final List<ConfigChangeListener> configChangeListeners = Lists.newCopyOnWriteArrayList();

    protected PropertiesFactory propertiesFactory;

    private static final ExecutorService executors;

    static {
        executors = Executors.newCachedThreadPool(QConfigThreadFactory.create("Config", true));
    }

    public AbstractConfig() {
        this.propertiesFactory = QConfigInjector.getInstance(PropertiesFactory.class);
    }

    @Override
    public String getProperty(String name) {
        return getProperty(name, null);
    }


    @Override
    public Byte getByteProperty(String name, Byte defaultValue) {
        return null;
    }

    @Override
    public Short getShortProperty(String name, Short defaultValue) {
        return null;
    }

    @Override
    public Integer getIntProperty(String name, Integer defaultValue) {
        return null;
    }

    @Override
    public Long getLongProperty(String name, Long defaultValue) {
        return null;
    }

    @Override
    public Float getFloatProperty(String name, Long defaultValue) {
        return null;
    }

    @Override
    public Double getDoubleProperty(String name, Double defaultValue) {
        return null;
    }

    @Override
    public Boolean getBooleanProperty(String name, Boolean defaultValue) {
        return null;
    }

    @Override
    public void addChangeListener(ConfigChangeListener configChangeListener) {
        this.configChangeListeners.add(configChangeListener);
    }

    @Override
    public void removeChangeListener(ConfigChangeListener configChangeListener) {
        this.configChangeListeners.remove(configChangeListener);
    }

    @Override
    public <T> T getProperty(String name, Function<String, T> function, T defaultValue) {
        return null;
    }

    List<ConfigChange> calcPropertyChanges(String namespace, Properties previous, Properties current) {
        if (previous == null) {
            previous = propertiesFactory.getPropertiesInstance();
        }
        if (current == null) {
            current = propertiesFactory.getPropertiesInstance();
        }
        Set<String> previousKeys = previous.stringPropertyNames();
        Set<String> currentKeys = current.stringPropertyNames();

        Set<String> commonKeys = Sets.intersection(previousKeys, currentKeys);
        Set<String> newKeys = Sets.difference(currentKeys, previousKeys);
        Set<String> removeKeys = Sets.difference(previousKeys, currentKeys);

        List<ConfigChange> configChanges = Lists.newArrayList();

        for (String newKey : newKeys) {
            configChanges.add(new ConfigChange(namespace, newKey, null, current.getProperty(newKey), PropertyChangeType.ADDED));
        }
        for (String removeKey : removeKeys) {
            configChanges.add(new ConfigChange(namespace, removeKey, previous.getProperty(removeKey), null, PropertyChangeType.DELETED));
        }
        for (String commonKey : commonKeys) {
            String previousProperty = previous.getProperty(commonKey);
            String currentProperty = current.getProperty(commonKey);
            if (Objects.equals(previousProperty, currentProperty)) {
                continue;
            }
            configChanges.add(new ConfigChange(namespace, commonKey, previous.getProperty(commonKey), current.getProperty(commonKey), PropertyChangeType.MODIFIED));
        }
        return configChanges;
    }

    protected void publishConfigChange(final String namespace, final Map<String, ConfigChange> changeMap) {
        for (ConfigChangeListener listener : configChangeListeners) {
            notifyAsync(listener, new ConfigChangeEvent(namespace, changeMap));
        }
    }

    protected void notifyAsync(final ConfigChangeListener listener, final ConfigChangeEvent event) {
        executors.submit(() -> {
            try {
                listener.onChange(event);
            } catch (Exception e) {
                log.error("notifyAsync has error! listener : {}", listener.getClass(), e);
            }
        });
    }


}
