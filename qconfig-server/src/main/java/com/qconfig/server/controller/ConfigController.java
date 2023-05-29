package com.qconfig.server.controller;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qconfig.common.dto.QConfig;
import com.qconfig.common.entity.ConfigContent;
import com.qconfig.common.entity.ServiceDTO;
import com.qconfig.server.discovery.DiscoveryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.net.http.HttpRequest;
import java.util.*;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/03/11:25
 */
@RestController
@RequestMapping("/config")
@Slf4j
public class ConfigController {

    Map<String, Map<String, String>> configCache = Maps.newConcurrentMap();

    List<String> notifyApp = Lists.newCopyOnWriteArrayList();


    @Autowired
    DiscoveryService discoveryService;

    public ConfigController() {
        configCache.put("test#test", Maps.asMap(Sets.newHashSet("test1, test2, test3"), key -> key + new Random(10000).nextInt()));
    }

    @RequestMapping("/getServices")
    public List<ServiceDTO> getServices(String address, String ip) {
        return discoveryService.getServiceInstances(ConfigContent.SERVER_NAME);
    }

    @RequestMapping
    public QConfig getQConfig(String appId,
                              String cluster,
                              String namespace,
                              @RequestParam(value = "releaseKey", defaultValue = "-1") Integer releaseKey,
                              HttpServletRequest request, HttpServletResponse response) {
        String key = generateKey(appId, namespace);
        if (notifyApp.contains(key) || releaseKey < 0) {
            notifyApp.remove(key);
            return new QConfig(appId, cluster, namespace, configCache.get(key), releaseKey + 1);
        }
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        return null;
    }


    @RequestMapping("/post")
    public void changeConfigChange(String appId, String namespace, String config) {
        Gson gson = new Gson();
        log.info(appId + ", " + namespace + ", " + gson.toJson(config));
        String key = generateKey(appId, namespace);
        configCache.put(key, gson.fromJson(config, Map.class));
        notifyApp.add(key);
    }

    private String generateKey(String appId, String namespace) {
        String key = appId + "#" + namespace;
        return key;
    }


}
