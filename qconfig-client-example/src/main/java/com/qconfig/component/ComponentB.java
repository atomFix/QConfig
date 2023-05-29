package com.qconfig.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/26/08:57
 */
@Component
public class ComponentB {

    @Value("${server.port}")
    private String port;

}
