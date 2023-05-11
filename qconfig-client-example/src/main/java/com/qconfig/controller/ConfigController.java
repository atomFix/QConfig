package com.qconfig.controller;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.qconfig.client.property.SpringValue;
import com.qconfig.client.property.SpringValueRegister;
import com.qconfig.client.util.SpringInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/11/17:44
 */
@RestController
@Slf4j
public class ConfigController {

    Gson gson = new Gson();

    @Value("${qonfig.client.enable}")
    public String clientEnable;

    private SpringValueRegister springValueRegister;

    public ConfigController() {
        springValueRegister = SpringInjector.getInstance(SpringValueRegister.class);
    }

    @RequestMapping("/getAllConfig")
    public String getAllConfig() {
        Map<BeanFactory, Multimap<String, SpringValue>> allValue = springValueRegister.getAllValue();
        if (null == allValue) {
            return "null";
        }
        Iterator<Multimap<String, SpringValue>> iterator = allValue.values().iterator();

        while (iterator.hasNext()) {
            Multimap<String, SpringValue> multimap = iterator.next();
            String json = gson.toJson(multimap);
            log.info("{} /n", json);
        }

        return gson.toJson(allValue);
    }


}
