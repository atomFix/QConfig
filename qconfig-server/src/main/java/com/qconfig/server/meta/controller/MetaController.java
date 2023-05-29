package com.qconfig.server.meta.controller;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.qconfig.common.entity.ServiceDTO;
import com.qconfig.common.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/25/16:36
 */
@RestController
@Slf4j
@RequestMapping("/config")
public class MetaController {

    private Gson gson = new Gson();

    @RequestMapping("/refresh")
    public List<ServiceDTO> refresh(String appId) {
        ServiceDTO serviceDTO = new ServiceDTO(appId, appId, "http://localhost:8083");
        return Lists.newArrayList(serviceDTO);
    }

}
