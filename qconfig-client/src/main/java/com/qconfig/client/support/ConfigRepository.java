package com.qconfig.client.support;

import com.qconfig.client.enums.ConfigSourceType;

import java.util.Properties;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/15:17
 */
public interface ConfigRepository {

    Properties getConfig();

    void addChangeListener(RepositoryChangeListener listener);

    void removeChangeListener(RepositoryChangeListener listener);

    ConfigSourceType getSourceType();

}
