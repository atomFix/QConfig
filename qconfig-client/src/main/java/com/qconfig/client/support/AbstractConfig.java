package com.qconfig.client.support;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qconfig.client.Config;
import com.qconfig.client.ConfigChangeListener;
import com.qconfig.client.model.ConfigChange;
import com.qconfig.client.model.ConfigChangeEvent;
import com.qconfig.client.util.QConfigInjector;
import com.qconfig.client.util.function.Functions;
import com.qconfig.common.enums.PropertyChangeType;
import com.qconfig.common.thread.QConfigThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/16:55
 */
@Slf4j
public abstract class AbstractConfig implements Config {

    private static final ExecutorService executors;

    static {
        executors = Executors.newCachedThreadPool(QConfigThreadFactory.create("Config", true));
    }

    private final List<ConfigChangeListener> configChangeListeners = Lists.newCopyOnWriteArrayList();
    protected PropertiesFactory propertiesFactory;

    private volatile Cache<String, Integer> inteagerCache;

    private volatile Cache<String, Long> longCache;
    private volatile Cache<String, Double> doubleCache;
    private volatile Cache<String, Short> shortCache;
    private volatile Cache<String, Byte> byteCache;
    private volatile Cache<String, Boolean> booleanCache;

    private volatile Cache<String, Float> floatCache;

    private final List<Cache> caches;

    private final AtomicLong configVersion;


    public AbstractConfig() {
        this.propertiesFactory = QConfigInjector.getInstance(PropertiesFactory.class);
        this.caches = Lists.newArrayList();
        this.configVersion = new AtomicLong();
    }

    @Override
    public String getProperty(String name) {
        return getProperty(name, null);
    }

    @Override
    public <T> T getProperty(String name, Function<String, T> parser, T defaultValue) {
        try {
            String value = getProperty(name, null);
            if (value != null) {
                return parser.apply(value);
            }
        } catch (Exception e) {
            log.error("getProperty format has error!", e);
        }

        return defaultValue;
    }

    @Override
    public Byte getByteProperty(String name, Byte defaultValue) {
        if (byteCache == null) {
            synchronized (this) {
                byteCache = newCache();
            }
        }
        return getValueFromCache(name, Functions.TO_BYTE_FUNCTION, byteCache, defaultValue);
    }

    @Override
    public Short getShortProperty(String name, Short defaultValue) {
        if (shortCache == null) {
            synchronized (this) {
                shortCache = newCache();
            }
        }
        return getValueFromCache(name, Functions.TO_SHORT_FUNCTION, shortCache, defaultValue);
    }

    @Override
    public Integer getIntProperty(String name, Integer defaultValue) {
        if (inteagerCache == null) {
            synchronized (this) {
                inteagerCache = newCache();
            }
        }
        return getValueFromCache(name, Functions.TO_INT_FUNCTION, inteagerCache, defaultValue);
    }

    @Override
    public Long getLongProperty(String name, Long defaultValue) {
        if (longCache == null) {
            synchronized (this) {
                longCache = newCache();
            }
        }
        return getValueFromCache(name, Functions.TO_LONG_FUNCTION, longCache, defaultValue);
    }

    @Override
    public Float getFloatProperty(String name, Float defaultValue) {
        if (floatCache == null) {
            synchronized (this) {
                floatCache = newCache();
            }
        }
        return getValueFromCache(name, Functions.TO_FLOAT_FUNCTION, floatCache, defaultValue);
    }

    @Override
    public Double getDoubleProperty(String name, Double defaultValue) {
        if (doubleCache == null) {
            synchronized (this) {
                doubleCache = newCache();
            }
        }
        return getValueFromCache(name, Functions.TO_DOUBLE_FUNCTON, doubleCache, defaultValue);
    }

    @Override
    public Boolean getBooleanProperty(String name, Boolean defaultValue) {
        if (booleanCache == null) {
            synchronized (this) {
                booleanCache = newCache();
            }
        }
        return getValueFromCache(name, Functions.TO_BOOLEAN_FUNCTIO, booleanCache, defaultValue);
    }

    @Override
    public void addChangeListener(ConfigChangeListener configChangeListener) {
        this.configChangeListeners.add(configChangeListener);
    }

    @Override
    public void removeChangeListener(ConfigChangeListener configChangeListener) {
        this.configChangeListeners.remove(configChangeListener);
    }


    private <T> T getValueFromCache(String key, Function<String, T> parser, Cache<String, T> cache, T defaultValue) {
        T result = cache.getIfPresent(key);
        if (result != null) {
            return result;
        }
        return getValueAndStoreToCache(key, parser, cache, defaultValue);
    }
    private <T> T getValueAndStoreToCache(String key, Function<String, T> parser, Cache<String, T> cache, T defaultValue) {
        long version = configVersion.get();
        String value = getProperty(key);

        if (value != null) {
            T result = parser.apply(value);

            if (result != null) {
                synchronized (this) {
                    if (configVersion.get() == version) {
                        cache.put(key, result);
                    }
                }
                return result;
            }
        }
        return defaultValue;
    }

    List<ConfigChange> calcPropertyChanges(String namespace, Properties previous, Properties current) {
        if (previous == null) {
            previous = propertiesFactory.getPropertiesInstance();
        }
        if (current == null) {
            current = propertiesFactory.getPropertiesInstance();
        }
        Set<String> previousKeys = previous.stringPropertyNames();
        Set<String> currentKeys = current.stringPropertyNames();

        Set<String> commonKeys = Sets.intersection(previousKeys, currentKeys);
        Set<String> newKeys = Sets.difference(currentKeys, previousKeys);
        Set<String> removeKeys = Sets.difference(previousKeys, currentKeys);

        List<ConfigChange> configChanges = Lists.newArrayList();

        for (String newKey : newKeys) {
            configChanges.add(new ConfigChange(namespace, newKey, null, current.getProperty(newKey), PropertyChangeType.ADDED));
        }
        for (String removeKey : removeKeys) {
            configChanges.add(new ConfigChange(namespace, removeKey, previous.getProperty(removeKey), null, PropertyChangeType.DELETED));
        }
        for (String commonKey : commonKeys) {
            String previousProperty = previous.getProperty(commonKey);
            String currentProperty = current.getProperty(commonKey);
            if (Objects.equals(previousProperty, currentProperty)) {
                continue;
            }
            configChanges.add(new ConfigChange(namespace, commonKey, previous.getProperty(commonKey), current.getProperty(commonKey), PropertyChangeType.MODIFIED));
        }
        return configChanges;
    }

    private <T> Cache<String, T> newCache() {
        Cache<String, T> cache = CacheBuilder.newBuilder().maximumSize(500).expireAfterAccess(1L, TimeUnit.MINUTES).build();
        caches.add(cache);
        return cache;
    }

    protected void publishConfigChange(final String namespace, final Map<String, ConfigChange> changeMap) {
        for (ConfigChangeListener listener : configChangeListeners) {
            notifyAsync(listener, new ConfigChangeEvent(namespace, changeMap));
        }
    }

    protected void notifyAsync(final ConfigChangeListener listener, final ConfigChangeEvent event) {
        executors.submit(() -> {
            try {
                listener.onChange(event);
            } catch (Exception e) {
                log.error("notifyAsync has error! listener : {}", listener.getClass(), e);
            }
        });
    }

    protected void claenCache() {
        synchronized (this) {
            for (Cache cache : caches) {
                cache.invalidateAll();
            }
        }
        configVersion.incrementAndGet();
    }

}
