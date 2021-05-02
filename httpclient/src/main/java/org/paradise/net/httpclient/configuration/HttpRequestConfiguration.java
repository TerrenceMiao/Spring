package org.paradise.net.httpclient.configuration;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Data
@ToString
public class HttpRequestConfiguration {

    private static final String DEFAULT_TIMEOUT = "PT10S";

    private String connectTimeout = DEFAULT_TIMEOUT;
    private String socketTimeout = DEFAULT_TIMEOUT;
    private String writeTimeout = DEFAULT_TIMEOUT;
    private boolean redirectsEnabled;

    public Duration connectTimeout() {
        return Duration.parse(connectTimeout);
    }

    public Duration socketTimeout() {
        return Duration.parse(socketTimeout);
    }

    public Duration writeTimeout() {
        return Duration.parse(writeTimeout);
    }

    public boolean redirectsEnabled() {
        return redirectsEnabled;
    }
}
