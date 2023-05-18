package com.qconfig.client.property;

import lombok.Data;
import org.springframework.core.MethodParameter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/10/20:47
 */
@Data
public class SpringValue {

    private WeakReference<Object> beanRef;

    private String beanName;

    private Field field;

    private String key;

    private MethodParameter methodParameter;

    private Class<?> targetType;

    private boolean isJson;

    private Type genericType;

    private String placeholder;


    public SpringValue(Object bean, String beanName, Field field, String key, String placeholder, boolean isJson) {
        beanRef = new WeakReference<>(bean);
        this.beanName = beanName;
        this.isJson = isJson;
        this.field = field;
        this.targetType = field.getClass();
        this.key = key;
        this.placeholder = placeholder;
        if (isJson) {
            genericType = field.getGenericType();
        }
    }

    public SpringValue(Object bean, String beanName, Method method, String key, String placeholder, boolean isJson) {
        beanRef = new WeakReference<>(bean);
        this.beanName = beanName;
        this.isJson = isJson;
        this.methodParameter = new MethodParameter(method, 0);
        this.targetType = method.getParameterTypes()[0];
        this.key = key;
        this.placeholder = placeholder;
        if (isJson) {
            genericType = method.getGenericParameterTypes()[0];
        }
    }

    public boolean hasUsage() {
        return beanRef.get() != null;
    }


    private boolean isFiled() {
        return field != null;
    }


    public void update(Object newValue) throws IllegalAccessException, InvocationTargetException {
        if (isFiled()) {
            updateFiled(newValue);
        } else {
            updateMethod(newValue);
        }
    }


    private void updateFiled(Object newValue) throws IllegalAccessException {
        Object bean = beanRef.get();
        if (bean == null) {
            return;
        }

        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(bean, newValue);
        field.setAccessible(accessible);
    }


    private void updateMethod(Object newValue) throws InvocationTargetException, IllegalAccessException {
        Object bean = beanRef.get();
        if (bean == null) {
            return;
        }

        methodParameter.getMethod().invoke(bean, newValue);
    }



}
