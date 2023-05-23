package com.qconfig.client.config;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.qconfig.client.Config;
import com.qconfig.client.ConfigChangeListener;
import com.qconfig.client.ConfigServers;
import com.qconfig.client.event.QConfigChangeEvent;
import com.qconfig.client.util.QConfigInjector;
import com.qconfig.common.entity.ConfigClientContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/16:09
 */
@Slf4j
public class PropertySourcesPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware, ApplicationEventPublisherAware, PriorityOrdered {

    private static final Multimap<Integer, String> NAMESPACE_MAP = LinkedListMultimap.create();

    private static final Set<BeanFactory> AUTO_UPDATE_INITIALIZED_BEAN_FACTORY = Sets.newConcurrentHashSet();

    private ConfigurableEnvironment environment;

    private ApplicationEventPublisher applicationEventPublisher;

    private final ConfigPropertySourceFactory configPropertySourceFactory;


    public PropertySourcesPostProcessor() {
        configPropertySourceFactory = QConfigInjector.getInstance(ConfigPropertySourceFactory.class);
    }


    public static void addNamespaces(Integer order, List<String> namespaces) {
        NAMESPACE_MAP.putAll(order, namespaces);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        initializePropertySource();
        initializeAutoUpdatePropertiesFeature(beanFactory);
    }

    private void initializeAutoUpdatePropertiesFeature(ConfigurableListableBeanFactory beanFactory) {
        if (!AUTO_UPDATE_INITIALIZED_BEAN_FACTORY.add(beanFactory)) {
            return;
        }

        ConfigChangeListener configChangeListener = changeEvent ->
            applicationEventPublisher.publishEvent(new QConfigChangeEvent(changeEvent));

        List<ConfigPropertySource> configPropertySource = configPropertySourceFactory.getAllConfigPropertySource();
        for (ConfigPropertySource propertySource : configPropertySource) {
            propertySource.addListener(configChangeListener);
        }

    }

    private void initializePropertySource() {
        if (environment.containsProperty(ConfigClientContent.QCONFIG_PROPERTY_SOURCE_NAME)) {
            return;
        }
        CompositePropertySource composite = new CompositePropertySource(ConfigClientContent.QCONFIG_PROPERTY_SOURCE_NAME);

        ImmutableSortedSet<Integer> orders = ImmutableSortedSet.copyOf(NAMESPACE_MAP.keySet());

        for (Integer order : orders) {
            for (String namespace : NAMESPACE_MAP.get(order)) {
                Config config = ConfigServers.getConfig(namespace);
                composite.addPropertySource(configPropertySourceFactory.create(namespace, config));
            }
        }

        NAMESPACE_MAP.clear();

        if (environment.getPropertySources().contains(ConfigClientContent.QCONFIG_BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
            environment.getPropertySources().addAfter(ConfigClientContent.QCONFIG_BOOTSTRAP_PROPERTY_SOURCE_NAME, composite);
        } else {
            environment.getPropertySources().addFirst(composite);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
