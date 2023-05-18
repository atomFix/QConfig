package com.qconfig.client.property;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qconfig.client.ConfigChangeListener;
import com.qconfig.client.model.ConfigChange;
import com.qconfig.client.model.ConfigChangeEvent;
import com.qconfig.client.util.SpringInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/16/00:06
 */
@Component
@Slf4j
public class AutoUpdateConfigChangeListener implements ApplicationContextAware, ConfigChangeListener {

    private ConfigurableBeanFactory beanFactory;

    private TypeConverter typeConverter;

    private SpringValueRegister springValueRegister;

    private PlaceholderHelper placeholderHelper;

    private Gson GSON;

    public AutoUpdateConfigChangeListener() {
        this.springValueRegister = SpringInjector.getInstance(SpringValueRegister.class);
        this.placeholderHelper = SpringInjector.getInstance(PlaceholderHelper.class);
        this.GSON = new Gson();
    }

    @Override
    public void onChange(ConfigChangeEvent configChangeEvent) {
        Set<String> configKey = configChangeEvent.getConfigKey();
        if (CollectionUtils.isEmpty(configKey)) {
            return;
        }

        for (String key : configKey) {

            Collection<SpringValue> springValues = springValueRegister.get(beanFactory, key);
            if (CollectionUtils.isEmpty(springValues)) {
                continue;
            }
            for (SpringValue springValue : springValues) {
                updateSpringValue(springValue);
            }
        }
    }

    private void updateSpringValue(SpringValue springValue) {
        Object value = placeholderHelper.resolvePropertyValue(beanFactory, springValue.getBeanName(), springValue.getPlaceholder());

        if (springValue.isJson()) {
            value = parseJsonValue(springValue, (String) value);
        } else {
            if (springValue.isFiled()) {
                value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType(), springValue.getField());
            } else {
                value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType(), springValue.getMethodParameter());
            }
        }

        try {
            springValue.update(value);
            log.info("update spring value success, springValue:{}, new value : {}", springValue,  value);
        } catch (Throwable e) {
            log.error("update spring value error, springValue:{}", springValue, e);
        }
    }

    private Object parseJsonValue(SpringValue springValue, String value) {
        try {
            return GSON.fromJson(value, springValue.getGenericType());
        } catch (JsonSyntaxException e) {
            log.warn("parse json error, springValue:{}", springValue);
            throw e;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        this.typeConverter = this.beanFactory.getTypeConverter();
    }
}
