package com.qc.server.service;

import com.qc.common.config.RefreshablePropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/04/30/09:13
 */
@Component
public class ServerDBPropertySource extends RefreshablePropertySource {



    public ServerDBPropertySource(String name, Map<String, Object> source) {

        super(name, source);

    }

    @Override
    public void refresh() {

    }
}
