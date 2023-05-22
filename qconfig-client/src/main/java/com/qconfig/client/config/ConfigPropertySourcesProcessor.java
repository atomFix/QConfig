package com.qconfig.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/22/18:39
 */
@Slf4j
public class ConfigPropertySourcesProcessor extends PropertySourcesPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.info("ConfigPropertySourcesProcessor init ");
    }
}
