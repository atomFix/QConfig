package com.qconfig.client.support;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.qconfig.client.property.*;
import com.qconfig.client.util.SpringInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/10/18:46
 */
@Component
@Slf4j
public class SpringValuePostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor, PriorityOrdered, BeanFactoryAware {

    private BeanFactory beanFactory;

    private PlaceholderHelper placeholderHelper;

    private SpringValueRegister springValueRegister;

    private Multimap<String, SpringValueDefinition> beanName2SpringValueDefinitions;



    public SpringValuePostProcessor() {
        placeholderHelper = SpringInjector.getInstance(PlaceholderHelper.class);
        springValueRegister = SpringInjector.getInstance(SpringValueRegister.class);
        beanName2SpringValueDefinitions = LinkedListMultimap.create();
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        processFields(bean, beanName, clazz);
        processMethods(bean, beanName, clazz);
        processXmlProperty(bean, beanName, clazz);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof BeanDefinitionRegistry) {
            beanName2SpringValueDefinitions = SpringValueDefinitionProcessor.getSpringValueDefinitions((BeanDefinitionRegistry) beanFactory);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void processFields(Object bean, String beanName, Class<?> clazz) {
        LinkedList<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(clazz, fields::add);
        for (Field field : fields) {
            processField(bean, beanName, field);
        }
    }

    private void processMethods(Object bean, String beanName, Class<?> clazz) {
        LinkedList<Method> methods = new LinkedList<>();
        ReflectionUtils.doWithMethods(clazz, methods::add);
        for (Method method : methods) {
            processMethod(bean, beanName, method);
        }
    }

    private void processXmlProperty(Object bean, String beanName, Class<?> clazz) {
        Collection<SpringValueDefinition> springValueDefinitions = beanName2SpringValueDefinitions.get(beanName);

        if (CollectionUtils.isEmpty(springValueDefinitions)) {
            return;
        }

        for (SpringValueDefinition springValueDefinition : springValueDefinitions) {
            String property = springValueDefinition.getProperty();
            Method writeMethod = Objects.requireNonNull(BeanUtils.getPropertyDescriptor(clazz, property)).getWriteMethod();
            if (writeMethod == null) {
                continue;
            }

            springValueRegister.register(beanFactory, springValueDefinition.getKey(),
                    new SpringValue(bean, beanName, writeMethod, springValueDefinition.getKey(), springValueDefinition.getPlaceholder(), false));
        }
    }

    private void processField(Object bean, String beanName, Field field) {
        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            return;
        }

        register(bean, beanName, field, value);
    }

    private void processMethod(Object bean, String beanName, Method method) {
        Value value = method.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        if (method.getAnnotation(Bean.class) != null) {
            return;
        }
        register(bean, beanName, method, value);
    }

    private void register(Object bean, String beanName, Member member, Value value) {
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(value.value());
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (String key : keys) {
            SpringValue springValue;
            if (member instanceof Field) {
                springValue = new SpringValue(bean, beanName, (Field) member, key, value.value(), false);
            } else if (member instanceof Method) {
                springValue = new SpringValue(bean, beanName, (Method) member, key, value.value(), false);
            } else {
                log.error("QConfig register has error! the target object's type is one error value! target class : {}", member.getClass());
                return;
            }

            springValueRegister.register(beanFactory, key, springValue);
        }
    }

}
