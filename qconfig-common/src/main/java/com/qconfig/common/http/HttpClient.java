package com.qconfig.common.http;

import java.lang.reflect.Type;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/25/09:52
 */
public interface HttpClient {

    /**
     * Do get operation for the http request.
     *
     * @param httpRequest  the request
     * @param responseType the response type
     * @return the response
     */
    <T> HttpResponse<T> doGet(HttpRequest httpRequest, final Class<T> responseType)
            throws Exception;

    /**
     * Do get operation for the http request.
     *
     * @param httpRequest  the request
     * @param responseType the response type
     * @return the response
     */
    <T> HttpResponse<T> doGet(HttpRequest httpRequest, final Type responseType)
            throws Exception;

}
