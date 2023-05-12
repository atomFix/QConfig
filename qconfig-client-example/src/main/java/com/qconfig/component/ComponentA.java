package com.qconfig.component;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/12/11:28
 */
@Data
//@Component
public class ComponentA {

    private String id;

    private String value;


    @Value("${server.port}")
    private String serverPort;

}
