package org.paradise.net.httpclient


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@ActiveProfiles('test')
@SpringBootTest(classes = [TestApplication.class], webEnvironment = RANDOM_PORT)
abstract class FunctionalSpec extends Specification implements ThirdPartyApiMockTrait {

    // Mock Server port also defined in "application-test.yml" file
    private static final int MOCK_SERVER_PORT = 11110

    @Autowired
    TestRestTemplate restTemplate


    def setupSpec() {
        startThirdPartyApiMock(MOCK_SERVER_PORT)
    }

    def setup() {
    }

    def cleanupSpec() {
        stopThirdPartyApiMock()
    }

    def cleanup() {
        resetThirdPartyApiMock()
    }
}
