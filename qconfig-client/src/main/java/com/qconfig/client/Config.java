package com.qconfig.client;


import com.qconfig.client.enums.ConfigSourceType;

import java.util.Set;
import java.util.function.Function;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/16:33
 */
public interface Config {

    Set<String> getPropertyNames();

    String getProperty(String name);

    String getProperty(String name, String defaultValue);


    Byte getByteProperty(String name, Byte defaultValue);

    Short getShortProperty(String name, Short defaultValue);

    Integer getIntProperty(String name, Integer defaultValue);

    Long getLongProperty(String name, Long defaultValue);

    Float getFloatProperty(String name, Float defaultValue);

    Double getDoubleProperty(String name, Double defaultValue);

    Boolean getBooleanProperty(String name, Boolean defaultValue);

    void addChangeListener(ConfigChangeListener configChangeListener);

    void removeChangeListener(ConfigChangeListener configChangeListener);

    <T> T getProperty(String name, Function<String, T> function, T defaultValue);

    ConfigSourceType getSourceType();
}
