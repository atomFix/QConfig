package com.qconfig.client.support;

import java.util.Properties;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/23/15:41
 */
public class DefaultPropertiesFactory implements PropertiesFactory {

    @Override
    public Properties getPropertiesInstance() {
        return new Properties();
    }
}
