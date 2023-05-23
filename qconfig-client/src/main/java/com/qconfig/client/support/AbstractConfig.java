package com.qconfig.client.support;

import com.google.common.collect.Lists;
import com.qconfig.client.Config;
import com.qconfig.client.ConfigChangeListener;

import java.util.List;
import java.util.function.Function;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/16:55
 */
public abstract class AbstractConfig implements Config {

    private final List<ConfigChangeListener> configChangeListeners = Lists.newCopyOnWriteArrayList();

    @Override
    public Object getProperty(String name, Object defaultValue) {
        Object val = getProperty(name);
        return val != null ? val : defaultValue;
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
}
