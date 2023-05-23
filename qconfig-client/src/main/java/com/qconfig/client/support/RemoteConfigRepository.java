package com.qconfig.client.support;

import com.qconfig.client.enums.ConfigSourceType;

import java.util.Properties;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/15:31
 */
public class RemoteConfigRepository extends AbstractConfigRepository {

    public RemoteConfigRepository() {
        this.trySync();
    }

    @Override
    public Properties getConfig() {
        return null;
    }

    @Override
    public ConfigSourceType getSourceType() {
        return ConfigSourceType.REMOTE;
    }

    @Override
    protected void sync() {

    }
}
