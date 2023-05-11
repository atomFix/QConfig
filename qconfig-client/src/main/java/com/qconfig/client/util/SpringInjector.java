package com.qconfig.client.util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.qconfig.client.property.PlaceholderHelper;
import com.qconfig.client.property.SpringValueRegister;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/11/18:10
 */
public class SpringInjector {

    private static final Object LOCK = new Object();
    private static volatile Injector springInjector;

    private static Injector getInjector() {
        if (springInjector == null) {
            synchronized (LOCK) {
                if (springInjector == null) {
                    springInjector = Guice.createInjector(new SpringModule());
                }
            }
        }
        return springInjector;
    }

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector().getInstance(clazz);
    }

    private static class SpringModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(PlaceholderHelper.class).in(Singleton.class);
            bind(SpringValueRegister.class).in(Singleton.class);
        }
    }


}
