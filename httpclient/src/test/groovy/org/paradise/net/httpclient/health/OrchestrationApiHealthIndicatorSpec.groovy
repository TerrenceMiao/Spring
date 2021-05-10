package org.paradise.net.httpclient.health

import com.google.common.collect.ImmutableMap
import org.apache.commons.lang3.RandomStringUtils
import org.paradise.net.httpclient.api.ApiHealth
import org.paradise.net.httpclient.ochestrationApi.OrchestrationApi
import org.springframework.boot.actuate.health.Status
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import retrofit2.Call
import retrofit2.OkHttpCall
import spock.lang.Specification
import spock.lang.Subject

import static java.lang.String.format
import static retrofit2.Response.error
import static retrofit2.Response.success

class OrchestrationApiHealthIndicatorSpec extends Specification {

    def username = RandomStringUtils.random(8)
    def password = RandomStringUtils.random(8)

    def headers = ImmutableMap.<String, String> builder()
            .put(HttpHeaders.AUTHORIZATION, format("Basic %s", Base64.getEncoder().encodeToString(format("%s:%s", username, password).getBytes())))
            .build()

    def mockOrchestrationApi = Mock(OrchestrationApi)

    @Subject
    def orchestrationApiHealthIndicator = new OrchestrationApiHealthIndicator(username, password, mockOrchestrationApi)

    def "should return health check up"() {

        given:
        mockOrchestrationApi.getHealth(headers) >> successfulResponse()

        when:
        def healthIndicator = orchestrationApiHealthIndicator.health()

        then:
        healthIndicator.status == Status.UP
    }

    def "should return health check down"() {

        given:
        mockOrchestrationApi.getHealth(headers) >> unsuccessfulResponse()

        when:
        def healthIndicator = orchestrationApiHealthIndicator.health()

        then:
        healthIndicator.status == Status.DOWN
    }

    def "should return health check down on error"() {

        given:
        mockOrchestrationApi.getHealth(headers) >> errorResponse()

        when:
        def healthIndicator = orchestrationApiHealthIndicator.health()

        then:
        healthIndicator.status == Status.DOWN
    }

    private Call<ApiHealth> successfulResponse() {
        Mock(Call) {
            execute() >> success(createResponseBody(Status.UP))
        }
    }

    private Call<ApiHealth> unsuccessfulResponse() {
        Mock(Call) {
            execute() >> error(HttpStatus.SERVICE_UNAVAILABLE.value(), new OkHttpCall.NoContentResponseBody(null, 0))
        }
    }

    private Call<ApiHealth> errorResponse() {
        Mock(Call) {
            execute() >> error(HttpStatus.BAD_REQUEST.value(), new OkHttpCall.NoContentResponseBody(null, 0))
        }
    }

    private static ApiHealth createResponseBody(Status status) {
        return new ApiHealth().setStatus(status.toString())
    }
}
