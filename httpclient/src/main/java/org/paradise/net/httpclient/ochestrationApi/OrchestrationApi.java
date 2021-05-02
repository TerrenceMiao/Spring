package org.paradise.net.httpclient.ochestrationApi;

import org.paradise.net.httpclient.api.ApiHealth;
import org.paradise.net.httpclient.api.CloseApiClientResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.OPTIONS;

import java.util.Map;

public interface OrchestrationApi {

    @GET("digitalworkspace/orchestration/v1/health")
    Call<ApiHealth> getHealth(
            @HeaderMap Map<String, String> context
    );

    @OPTIONS("<empty>")
    CloseApiClientResponse close();
}
