package com.qconfig.server.controller;

import com.qconfig.common.entity.ConfigContent;
import com.qconfig.common.entity.ServiceDTO;
import com.qconfig.server.discovery.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/03/11:25
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    DiscoveryService discoveryService;

    @RequestMapping("/getServices")
    public List<ServiceDTO > getServices(String address, String ip){
        return discoveryService.getServiceInstances(ConfigContent.SERVER_NAME);
    }

}
