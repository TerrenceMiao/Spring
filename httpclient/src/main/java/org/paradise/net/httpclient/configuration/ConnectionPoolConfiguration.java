package org.paradise.net.httpclient.configuration;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Data
@ToString
public class ConnectionPoolConfiguration {

    private int maxIdleConnections;
    private Duration keepAlive;

    public int maxIdleConnections() {
        return 0;
    }

    public Duration keepAlive() {
        return Duration.ofSeconds(10);
    }
}
