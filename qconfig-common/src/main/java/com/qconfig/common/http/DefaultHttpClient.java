package com.qconfig.common.http;

import com.google.common.base.Function;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Description:
 * @author: liukairong1
 * @date: 2023/05/25/09:59
 */
public class DefaultHttpClient implements HttpClient{

    private static final Gson GSON = new Gson();

    @Override
    public <T> HttpResponse<T> doGet(HttpRequest httpRequest, Class<T> responseType) throws Exception {
        return doGetWithSerializeFunction(httpRequest, input -> GSON.fromJson(input, responseType));
    }

    @Override
    public <T> HttpResponse<T> doGet(HttpRequest httpRequest, Type responseType) throws Exception {
        return doGetWithSerializeFunction(httpRequest, input -> GSON.fromJson(input, responseType));
    }

    private <T> HttpResponse<T> doGetWithSerializeFunction(HttpRequest httpRequest,
                                                           Function<String, T> serializeFunction) throws Exception {
        InputStreamReader isr = null;
        InputStreamReader esr = null;
        int statusCode;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(httpRequest.getUrl()).openConnection();

            conn.setRequestMethod("GET");

            Map<String, String> headers = httpRequest.getHeaders();
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            int connectTimeout = httpRequest.getConnectTimeout();
            if (connectTimeout < 0) {
                connectTimeout = 3000;
            }

            int readTimeout = httpRequest.getReadTimeout();
            if (readTimeout < 0) {
                readTimeout = 3000;
            }

            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);

            conn.connect();

            statusCode = conn.getResponseCode();
            String response;

            try {
                isr = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
                response = CharStreams.toString(isr);
            } catch (IOException ex) {
                /**
                 * according to https://docs.oracle.com/javase/7/docs/technotes/guides/net/http-keepalive.html,
                 * we should clean up the connection by reading the response body so that the connection
                 * could be reused.
                 */
                InputStream errorStream = conn.getErrorStream();

                if (errorStream != null) {
                    esr = new InputStreamReader(errorStream, StandardCharsets.UTF_8);
                    try {
                        CharStreams.toString(esr);
                    } catch (IOException ioe) {
                        //ignore
                    }
                }

                // 200 and 304 should not trigger IOException, thus we must throw the original exception out
                if (statusCode == 200 || statusCode == 304) {
                    throw ex;
                }
                // for status codes like 404, IOException is expected when calling conn.getInputStream()
                throw new Exception(String.valueOf(statusCode), ex);
            }

            if (statusCode == 200) {
                return new HttpResponse<>(statusCode, serializeFunction.apply(response));
            }

            if (statusCode == 304) {
                return new HttpResponse<>(statusCode, null);
            }
        } catch (Throwable ex) {
            throw new Exception("Could not complete get operation" + ex.getMessage());
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ex) {
                    // ignore
                }
            }

            if (esr != null) {
                try {
                    esr.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }

        throw new Exception(
                String.format("Get operation failed for %s, code status : %s", httpRequest.getUrl(), statusCode));
    }

}
