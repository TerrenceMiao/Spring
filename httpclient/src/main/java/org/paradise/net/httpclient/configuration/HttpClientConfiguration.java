package org.paradise.net.httpclient.configuration;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ToString
public class HttpClientConfiguration {

    private HttpRequestConfiguration httpRequestConfiguration;
    private String loggerName;

    public String loggerName() {
        return loggerName;
    }

    public HttpRequestConfiguration request() {
        return httpRequestConfiguration;
    }
}
