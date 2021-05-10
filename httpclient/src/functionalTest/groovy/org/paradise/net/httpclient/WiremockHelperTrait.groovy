package org.paradise.net.httpclient

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import groovy.text.GStringTemplateEngine

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.containing
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.matchingXPath
import static com.github.tomakehurst.wiremock.client.WireMock.notMatching
import static com.github.tomakehurst.wiremock.client.WireMock.request
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching

trait WiremockHelperTrait {

    static class LogRequest implements Request {
        @Delegate
        LoggedRequest request

        def LogRequest(LoggedRequest request) {
            this.request = request
        }

        @Override
        String toString() {
            return request.getUrl()
        }
    }

    def wiremockReturns(bindings) {
        if (bindings?.response?.fault == 'CONNECTION_REFUSED') {
            stopThirdPartyApiMock()
            return
        }

        def list = [] + bindings
        def scenario = Scenario.STARTED

        list.eachWithIndex { Map values, index ->
            MappingBuilder mappingBuilder = buildRequest(values.request as Map, scenario, index)
            ResponseDefinitionBuilder responseBuilder = buildResponse(values.response as Map)
            stubFor(mappingBuilder.willReturn(responseBuilder))
        }
    }

    ResponseDefinitionBuilder buildResponse(Map response) {
        def responseBuilder = aResponse()

        // socket timeout
        if ('SOCKET_TIMEOUT' == response.fault) {
            responseBuilder.withStatus(response.status as int).withFixedDelay(response.requestTimeout + 1000 as int)
            return responseBuilder
        }

        // other connection faults
        if (response.fault) {
            responseBuilder.withFault(Fault.valueOf(response.fault as String))
            return responseBuilder
        }

        if (response.delay) {
            responseBuilder.withFixedDelay(response.delay as int)
        }

        // http responses 4xx, 5xx or 2xx
        String body = loadBodyFromFile(response)

        responseBuilder.withBody(body ?: '').withStatus(response.status ?: 200)

        (response.headers ?: [:]).each { name, value ->
            responseBuilder.withHeader(name as String, value as String)
        }

        responseBuilder
    }

    String loadBodyFromFile(Map bindings) {
        if (bindings.bodySkip) {
            return bindings.body
        }

        if (bindings.bodyPath || bindings.body) {
            String body = bindings.body

            if (bindings.bodyPath) {
                body = ThirdPartyApiMockTrait.class.getResourceAsStream(bindings.bodyPath as String).text
            }

            return new GStringTemplateEngine().createTemplate(body).make(bindings).toString();
        }

        return null
    }

    MappingBuilder buildRequest(Map bindings, String scenario, index) {
        def mappingBuilder = request(bindings.method ?: 'GET', urlPathMatching(bindings.url ?: ''))

        // set headers
        (bindings.headers ?: [:]).each { name, value ->
            mappingBuilder.withHeader(name as String, matching(value as String))
        }
        // set query params
        (bindings.params ?: [:]).each { name, value ->
            mappingBuilder.withQueryParam(name as String, matching(value as String))
        }
        // body
        def content = loadBodyFromFile(bindings)
        if (bindings.json && content) {
            mappingBuilder.withRequestBody(equalToJson(content, true, true))
        }
        if (bindings.str && content) {
            mappingBuilder.withRequestBody(containing(bindings.str as String))
        }

        if (bindings.regexs) {
            List regexs = [] + bindings.regexs
            regexs.each {
                mappingBuilder.withRequestBody(notMatching(it as String))
            }
        }

        if (bindings.xpaths) {
            List xpaths = [] + bindings.xpaths
            xpaths.each {
                mappingBuilder.withRequestBody(matchingXPath(it as String))
            }
        }

        mappingBuilder
    }
}
