package com.qconfig.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/24/17:11
 */
@Data
@AllArgsConstructor
@NonNull
public class QConfig {

    private String appId;

    private String cluster;

    private String namespace;

    private Map<String, String> configurations;

    private Integer releaseKey;
}
