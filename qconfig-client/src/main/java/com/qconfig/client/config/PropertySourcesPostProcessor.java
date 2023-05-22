package com.qconfig.client.config;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.qconfig.client.util.QConfigInjector;
import com.qconfig.common.entity.ConfigClientContent;
import org.springframework.beans.BeansException;
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

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/16:09
 */
public class PropertySourcesPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware, ApplicationEventPublisherAware, PriorityOrdered {

    private static final Multimap<Integer, String> NAMESPACE_MAP = LinkedListMultimap.create();

    private ConfigurableEnvironment environment;

    private ApplicationEventPublisher applicationEventPublisher;

    private ConfigPropertySourceFactory configPropertySourceFactory;

    public PropertySourcesPostProcessor() {
        configPropertySourceFactory = QConfigInjector.getInstance(ConfigPropertySourceFactory.class);
    }


    public static void addNamespaces(Integer order, List<String> namespaces) {
        NAMESPACE_MAP.putAll(order, namespaces);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        initializePropertySource();
    }

    private void initializePropertySource() {
        if (environment.containsProperty(ConfigClientContent.QCONFIG_PROPERTY_SOURCE_NAME)) {
            return;
        }
        CompositePropertySource composite = new CompositePropertySource(ConfigClientContent.QCONFIG_PROPERTY_SOURCE_NAME);


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
