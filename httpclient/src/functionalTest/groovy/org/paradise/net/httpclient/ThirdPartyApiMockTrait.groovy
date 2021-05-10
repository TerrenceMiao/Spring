package org.paradise.net.httpclient

import com.github.tomakehurst.wiremock.WireMockServer
import org.paradise.net.httpclient.orchestrationApi.OrchestrationApiMockTrait

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor
import static com.github.tomakehurst.wiremock.client.WireMock.reset
import static com.github.tomakehurst.wiremock.client.WireMock.resetAllRequests
import static com.github.tomakehurst.wiremock.client.WireMock.resetAllScenarios

trait ThirdPartyApiMockTrait implements OrchestrationApiMockTrait {

    static WireMockServer wiremock

    def startThirdPartyApiMock(int thirdPartyApiPort) {
        if (!wiremock) {
            wiremock = new WireMockServer(wireMockConfig().port(thirdPartyApiPort))
            wiremock.start()
            configureFor(thirdPartyApiPort)
        }
    }

    def resetThirdPartyApiMock() {
        if (wiremock) {
            reset()
            resetAllRequests()
            resetAllScenarios()
        }
    }

    def stopThirdPartyApiMock() {
        if (wiremock) {
            wiremock.stop()
            wiremock = null
        }
    }
}

