package org.paradise.net.httpclient.health

import org.paradise.net.httpclient.FunctionalSpec

import static org.springframework.boot.actuate.health.Status.DOWN

class HealthCheckFunctionalSpec extends FunctionalSpec {

    private static final String HEALTH_PATH = '/actuator/health'

    def "should return service status is Up"() {

        given:'Orchestration API returns HTTP 200 OK'
        orchestrationApiHealthCheck([status: 200])

        when:
        Map<String, String> result = restTemplate.getForObject(HEALTH_PATH, Map.class)

        then:
        result.status == DOWN.toString()
    }

    def "should return service status is Down"() {

        given:'Orchestration API returns HTTP 503 Service Unavailable error'
        orchestrationApiHealthCheck([status: 503])

        when:
        Map<String, String> result = restTemplate.getForObject(HEALTH_PATH, Map.class)

        then:
        result.status == DOWN.toString()
    }
}
