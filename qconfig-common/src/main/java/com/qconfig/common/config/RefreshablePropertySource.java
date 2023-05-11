package com.qconfig.common.config;

import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/29/11:06
 */
public abstract class RefreshablePropertySource extends MapPropertySource {

    public RefreshablePropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    public abstract void refresh();

    @Override
    public Object getProperty(String name) {
        return super.getProperty(name);
    }
}
