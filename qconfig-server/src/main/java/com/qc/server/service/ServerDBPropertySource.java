package com.qc.server.service;

import com.qc.biz.repository.ServerConfigRepository;
import com.qc.common.config.RefreshablePropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/30/09:13
 */
@Component
public class ServerDBPropertySource extends RefreshablePropertySource {

    @Autowired
    public ServerConfigRepository serverConfigRepository;


    public ServerDBPropertySource(String name, Map<String, Object> source) {
        super(name, source);

    }

    @Override
    public void refresh() {

    }
}
