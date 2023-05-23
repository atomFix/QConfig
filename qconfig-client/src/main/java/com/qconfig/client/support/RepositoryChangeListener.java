package com.qconfig.client.support;

import java.util.Properties;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/14:44
 */
public interface RepositoryChangeListener {

    void onRepositoryChange(String namespace, Properties properties);

}
