package com.qconfig.client.event;

import com.qconfig.client.model.ConfigChangeEvent;
import org.springframework.context.ApplicationEvent;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/12:32
 */
public class QConfigChangeEvent extends ApplicationEvent {

    public QConfigChangeEvent(ConfigChangeEvent source) {
        super(source);
    }

    public ConfigChangeEvent getConfigChangeEvent() {
        return (ConfigChangeEvent) getSource();
    }
}
