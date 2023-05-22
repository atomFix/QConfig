package com.qconfig.client.config;

import com.qconfig.client.Config;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/16:30
 */
public class ConfigPropertySource extends EnumerablePropertySource<Config> {

    private final String[] DEFAULT_EMPTY_ARRAY = new String[]{};


    public ConfigPropertySource(String name, Config source) {
        super(name, source);
    }

    @Override
    public String[] getPropertyNames() {
        Set<String> propertyNames = this.source.getPropertyNames();
        if (CollectionUtils.isEmpty(propertyNames)) {
            return DEFAULT_EMPTY_ARRAY;
        }
        return propertyNames.toArray(new String[0]);
    }

    @Override
    public Object getProperty(String name) {
        return this.source.getProperty(name);
    }
}
