package com.qconfig.common.http;

import lombok.Data;

import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/25/09:54
 */

@Data
public class HttpRequest {

    private final String url;
    private Map<String, String> headers;
    private int connectTimeout;
    private int readTimeout;

    /**
     * Create the request for the url.
     * @param url the url
     */
    public HttpRequest(String url) {
        this.url = url;
        connectTimeout = -1;
        readTimeout = -1;
    }

}
