package org.paradise.net.httpclient.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.paradise.net.httpclient.api.TimeoutMonitorConfiguration;
import org.paradise.net.httpclient.interceptor.CircuitBreakerInterceptor;
import org.paradise.net.httpclient.interceptor.Slf4jLoggingInterceptor;
import org.paradise.net.httpclient.ochestrationApi.OrchestrationApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.net.Proxy;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.net.Proxy.NO_PROXY;
import static java.time.Duration.parse;

@Configuration
@ConfigurationProperties(prefix = OrchestrationApiConfiguration.PREFIX_ORCHESTRATION_API)
@Getter
@Setter
@Slf4j
public class OrchestrationApiConfiguration {

    static final String PREFIX_ORCHESTRATION_API = "orchestration-api.service";

    private String endpoint;
    private String apiKey;
    private String overallTimeout;
    private String cacheExpiry;
    private int failureThreshold;
    private String sleepWindow;
    private String whitelist;
    private HttpClientConfiguration httpclient;

    private Retrofit.Builder retrofitBuilder;
    private OkHttpClient.Builder okHttpClientBuilder;
    private OkHttpClient okHttpClient;
    private ScheduledExecutorService timeoutMonitor;

    public String endpoint() {
        return endpoint;
    }

    public String apiKey() {
        return apiKey;
    }

    public String apiKeyHeader() {
        return "API-KEY";
    }

    public Duration overallTimeout() {
        return parse(overallTimeout);
    }

    public TimeoutMonitorConfiguration timeoutMonitor() {
        return () -> "TimeoutMonitor-";
    }

    public ProxyConfiguration proxyConfiguration() {
        return new ProxyConfiguration();
    }

    public HttpClientConfiguration httpclient() {
        return httpclient;
    }

    @Bean(destroyMethod="")
    public OrchestrationApi orchestrationApi(Converter.Factory converterFactory) {

        okHttpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(overallTimeout().getSeconds(), TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new Slf4jLoggingInterceptor(log))
                .addInterceptor(new CircuitBreakerInterceptor(failureThreshold, parse(sleepWindow), whitelist))
                .hostnameVerifier((s, sslSession) -> true)
                .proxy(proxy());

        if (proxyConfiguration().hasAuthentication()) {
            okHttpClientBuilder.proxyAuthenticator(proxyConfiguration().buildAuthenticator());
        }

        okHttpClient = okHttpClientBuilder.build();

        retrofitBuilder = new Retrofit.Builder();

        return retrofitBuilder
                .baseUrl(endpoint())
                .client(okHttpClient)
                .addConverterFactory(converterFactory)
                .build()
                .create(OrchestrationApi.class);
    }

    public Proxy proxy() {

        if (proxyConfiguration().hasProxy()) {
            return proxyConfiguration().buildProxy();
        }

        return NO_PROXY;
    }
}
