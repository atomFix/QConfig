package com.qconfig.client;

import com.qconfig.client.model.ConfigChangeEvent;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/16/00:07
 */
public interface ConfigChangeListener {

    void onChange(ConfigChangeEvent configChangeEvent);

}
