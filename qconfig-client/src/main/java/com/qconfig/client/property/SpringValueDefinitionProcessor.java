package com.qconfig.client.property;

import com.google.common.collect.*;
import com.qconfig.client.util.SpringInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/11/20:03
 */
@Component
@Slf4j
public class SpringValueDefinitionProcessor implements BeanDefinitionRegistryPostProcessor, Ordered {

    private final PlaceholderHelper placeholderHelper;

    private static final Map<BeanDefinitionRegistry, Multimap<String, SpringValueDefinition>> beanName2SpringValueDefinitions = Maps.newConcurrentMap();

    private static final Set<BeanDefinitionRegistry> PROPERTY_VALUES_PROCESSED_BEAN_FACTORIES = Sets.newConcurrentHashSet();


    public SpringValueDefinitionProcessor() {
        this.placeholderHelper = SpringInjector.getInstance(PlaceholderHelper.class);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        processPropertyValues(beanDefinitionRegistry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    public static Multimap<String, SpringValueDefinition> getSpringValueDefinitions(BeanDefinitionRegistry beanDefinitionRegistry) {
        return beanName2SpringValueDefinitions.computeIfAbsent(beanDefinitionRegistry, k -> LinkedListMultimap.create());
    }

    private void processPropertyValues(BeanDefinitionRegistry beanDefinitionRegistry) {
        if (!PROPERTY_VALUES_PROCESSED_BEAN_FACTORIES.add(beanDefinitionRegistry)) {
            return;
        }
        if (!beanName2SpringValueDefinitions.containsKey(beanDefinitionRegistry)) {
            beanName2SpringValueDefinitions.put(beanDefinitionRegistry, LinkedListMultimap.create());
        }

        Multimap<String, SpringValueDefinition> springValueDefinitionMap = beanName2SpringValueDefinitions.get(beanDefinitionRegistry);

        String[] definitionNames = beanDefinitionRegistry.getBeanDefinitionNames();

        for (String beanName : definitionNames) {
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanName);
            MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
            List<PropertyValue> propertyValueList = propertyValues.getPropertyValueList();
            for (PropertyValue propertyValue : propertyValueList) {
                Object value = propertyValue.getValue();
                if (!(value instanceof TypedStringValue)) {
                    continue;
                }
                String placeholder = ((TypedStringValue) value).getValue();

                Set<String> keys = placeholderHelper.extractPlaceholderKeys(placeholder);
                if (keys.isEmpty()) {
                    return;
                }

                for (String key : keys) {
                    springValueDefinitionMap.put(beanName, new SpringValueDefinition(key, placeholder, propertyValue.getName()));
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
