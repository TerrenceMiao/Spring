package org.paradise.net.httpclient.configuration;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ToString
public class HttpClientConfiguration {

    private String loggerName;
    private ConnectionPoolConfiguration connectionPool;
    private HttpRequestConfiguration httpRequestConfig;
    private DispatcherConfiguration dispatcher;

    public String loggerName() {
        return loggerName;
    }

    public ConnectionPoolConfiguration connectionPool() {
        return new ConnectionPoolConfiguration();
    }

    public HttpRequestConfiguration httpRequestConfig() {
        return new HttpRequestConfiguration();
    }

    public DispatcherConfiguration dispatcher() {
        return new DispatcherConfiguration();
    }
}
