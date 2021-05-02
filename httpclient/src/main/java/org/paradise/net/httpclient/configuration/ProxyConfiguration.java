package org.paradise.net.httpclient.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Authenticator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

import static java.net.Proxy.Type.HTTP;
import static okhttp3.Credentials.basic;
import static org.springframework.util.StringUtils.hasText;

@Configuration
@ConfigurationProperties(prefix = ProxyConfiguration.PREFIX_PROXY)
@Data
@Getter
@Setter
public class ProxyConfiguration {

    static final String PREFIX_PROXY = "proxy";

    private String host;
    private int port;
    private String username;
    private String password;

    boolean hasProxy() {
        return hasText(getHost()) && getPort() > 0;
    }

    boolean hasAuthentication() {
        return hasProxy() && hasText(getUsername()) && hasText(getPassword());
    }

    Proxy buildProxy() {
        return new Proxy(HTTP, new InetSocketAddress(getHost(), getPort()));
    }

    Authenticator buildAuthenticator() {
        return (route, response) -> {
            String credential = basic(getUsername(), getPassword());
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };
    }
}
