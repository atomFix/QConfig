package com.qconfig.client.support;

import com.qconfig.client.Config;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/10:05
 */
public interface ConfigManager {

    Config getConfig(String namespace);

}
