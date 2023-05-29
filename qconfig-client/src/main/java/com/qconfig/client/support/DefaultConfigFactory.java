package com.qconfig.client.support;

import com.qconfig.client.Config;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/10:25
 */
public class DefaultConfigFactory implements ConfigFactory {

    @Override
    public Config create(String namespace) {
        return new DefaultConfig(namespace, new RemoteConfigRepository(namespace));
    }
}