package com.qconfig.client.property;

import lombok.Data;
import org.springframework.core.MethodParameter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
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


    public SpringValue(Object bean, String beanName, Field field, String key, boolean isJson) {
        beanRef = new WeakReference<>(bean);
        this.beanName = beanName;
        this.isJson = isJson;
        this.field = field;
        this.targetType = field.getClass();
        this.key = key;
        if (isJson) {
            genericType = field.getGenericType();
        }
    }

    public SpringValue(Object bean, String beanName, Method method, String key, boolean isJson) {
        beanRef = new WeakReference<>(bean);
        this.beanName = beanName;
        this.isJson = isJson;
        this.methodParameter = new MethodParameter(method, 0);
        this.targetType = method.getParameterTypes()[0];
        this.key = key;
        if (isJson) {
            genericType = method.getGenericParameterTypes()[0];
        }
    }

    public boolean hasUsage() {
        return beanRef.get() != null;
    }

}
