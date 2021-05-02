package org.paradise.net.httpclient.interceptor;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;

import java.io.IOException;

public class Slf4jLoggingInterceptor implements Interceptor {

    private final HttpLoggingInterceptor httpLoggingInterceptor;

    public Slf4jLoggingInterceptor(Logger logger) {
        httpLoggingInterceptor = new HttpLoggingInterceptor(logger::info);
        httpLoggingInterceptor.setLevel(toOkHttpLoggerLevel(logger));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return httpLoggingInterceptor.intercept(chain);
    }

    private HttpLoggingInterceptor.Level toOkHttpLoggerLevel(Logger logger) {

        if (logger.isTraceEnabled()) {
            return HttpLoggingInterceptor.Level.BODY;
        } else if (logger.isDebugEnabled()) {
            return HttpLoggingInterceptor.Level.HEADERS;
        } else if (logger.isInfoEnabled()) {
            return HttpLoggingInterceptor.Level.BASIC;
        }

        return HttpLoggingInterceptor.Level.NONE;
    }
}
