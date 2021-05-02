package org.paradise.net.httpclient.configuration;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Data
@ToString
public class DispatcherConfiguration {

    private int corePoolSize;
    private int maxPoolSize;
    private String namePrefix;
    private Duration keepAlive;
    private int maxRequests;
    private int maxRequestsPerHost;
    private int queueSize;

    public int corePoolSize() {
        return 0;
    }

    public int maxPoolSize() {
        return Integer.MAX_VALUE;
    }

    public String namePrefix() {
        return namePrefix;
    }

    public Duration keepAlive() {
        return Duration.ofSeconds(60);
    }

    public int maxRequests() {
        return 64;
    }

    public int maxRequestsPerHost() {
        return 64;
    }

    public int queueSize() {
        return Integer.MAX_VALUE;
    }
}
