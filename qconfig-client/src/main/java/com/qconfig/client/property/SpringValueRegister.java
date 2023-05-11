package com.qconfig.client.property;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.qconfig.common.thread.QConfigThreadFactory;
import org.springframework.beans.factory.BeanFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/11/09:23
 */
public class SpringValueRegister {
    private final Map<BeanFactory, Multimap<String, SpringValue>> springValues = Maps.newConcurrentMap();
    private final Object LOCK = new Object();
    AtomicBoolean initialized = new AtomicBoolean(false);

    public void register(BeanFactory beanFactory, String key, SpringValue value) {
        if (!springValues.containsKey(beanFactory)) {
            synchronized (LOCK) {
                springValues.put(beanFactory, Multimaps.synchronizedMultimap(LinkedHashMultimap.create()));
            }
        }

        springValues.get(beanFactory).put(key, value);

        if (initialized.compareAndSet(false, true)) {
            initialize();
        }
    }

    public Collection<SpringValue> get(BeanFactory beanFactory, String key) {
        Multimap<String, SpringValue> beanFactorySpringValues = springValues.get(beanFactory);
        if (beanFactorySpringValues == null) {
            return null;
        }
        return beanFactorySpringValues.get(key);
    }

    public Map<BeanFactory, Multimap<String, SpringValue>> getAllValue() {
        return springValues;
    }


    public void initialize() {
        Executors.newSingleThreadScheduledExecutor(QConfigThreadFactory.create("SpringValueRegister", true))
                .scheduleAtFixedRate(this::cleanValue, 5, 5, TimeUnit.MINUTES);
    }

    public void cleanValue() {
        Iterator<Multimap<String, SpringValue>> values = springValues.values().iterator();
        while (!Thread.currentThread().isInterrupted() && values.hasNext()) {
            Multimap<String, SpringValue> next = values.next();

            Iterator<Map.Entry<String, SpringValue>> springValues = next.entries().iterator();
            while (springValues.hasNext()) {
                SpringValue value = springValues.next().getValue();
                if (!value.hasUsage()) {
                    springValues.remove();
                }
            }
        }
    }

}
