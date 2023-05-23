package com.qconfig.client.util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.qconfig.client.config.ConfigPropertySourceFactory;
import com.qconfig.client.support.*;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/17:36
 */
public class QConfigInjector {

    private static final Object LOCK = new Object();

    private static Injector injector;

    private static Injector getInjector() {
        if (injector == null) {
            synchronized (LOCK) {
                if (injector == null) {
                    injector = Guice.createInjector(new QConfigModule());
                }
            }
        }
        return injector;
    }

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector().getInstance(clazz);
    }

    private static class QConfigModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ConfigPropertySourceFactory.class).in(Singleton.class);
            bind(ConfigFactoryManager.class).to(DefaultConfigFactoryManager.class).in(Singleton.class);
            bind(ConfigFactory.class).to(DefaultConfigFactory.class).in(Singleton.class);
            bind(ConfigManager.class).to(DefaultConfigManager.class).in(Singleton.class);
            bind(PropertiesFactory.class).to(DefaultPropertiesFactory.class).in(Singleton.class);
        }
    }
}
