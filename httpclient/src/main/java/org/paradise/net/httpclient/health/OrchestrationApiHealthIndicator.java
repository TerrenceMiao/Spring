package org.paradise.net.httpclient.health;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.paradise.net.httpclient.api.ApiHealth;
import org.paradise.net.httpclient.ochestrationApi.OrchestrationApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static java.lang.String.format;

@Slf4j
@Component
public class OrchestrationApiHealthIndicator extends AbstractHealthIndicator {

    private static final String DETAIL = "detail";

    private final OrchestrationApi orchestrationApi;
    private final Map<String, String> headers;

    @Autowired
    public OrchestrationApiHealthIndicator(@Value("${api-gateway.service.credentials.admin.username}") String username,
                                           @Value("${api-gateway.service.credentials.admin.password}") String password,
                                           OrchestrationApi orchestrationApi) {
        this.orchestrationApi = orchestrationApi;
        this.headers = ImmutableMap.<String, String>builder()
                .put(HttpHeaders.AUTHORIZATION, createBasicAuth(username, password))
                .build();
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {

        try {
            Response<ApiHealth> response = orchestrationApi.getHealth(headers).execute();

            String detail = String.format("Response code: %s; Response message: [%s]", response.code(), response.message());

            if (isSuccessResponse(response.code())) {
                builder.up().withDetail(DETAIL, detail);
            } else {
                builder.down().withDetail(DETAIL, detail);
            }
        } catch (IOException e) {
            builder.down(e).withDetail(DETAIL, e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        orchestrationApi.close();
    }

    private String createBasicAuth(String username, String password) {
        return format("Basic %s", Base64.getEncoder().encodeToString(format("%s:%s", username, password).getBytes()));
    }

    private boolean isSuccessResponse(int status) {
        return HttpStatus.valueOf(status).is2xxSuccessful();
    }
}
