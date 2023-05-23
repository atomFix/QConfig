package com.qconfig.client.support;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/10:14
 */
public interface ConfigFactoryManager {

    ConfigFactory getFactory(String namespace);

}
