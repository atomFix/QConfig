package com.qconfig.client.support;

import com.qconfig.client.Config;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/10:10
 */
public interface ConfigFactory {

    Config create(String namespace);

}
