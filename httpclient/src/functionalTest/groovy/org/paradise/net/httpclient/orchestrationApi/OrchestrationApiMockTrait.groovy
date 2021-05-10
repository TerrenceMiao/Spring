package org.paradise.net.httpclient.orchestrationApi

import org.paradise.net.httpclient.WiremockHelperTrait

trait OrchestrationApiMockTrait extends WiremockHelperTrait {

    public static final ORCHESTRATION_HEALTH_CHECK_PATH = '/digitalworkspace/orchestration/v1/health'

    def orchestrationApiHealthCheck(Map bindings) {
        def defaults = [
        ] << bindings

        wiremockReturns([
            request : [
                method: 'GET',
                url   : "${ORCHESTRATION_HEALTH_CHECK_PATH}",
            ],
            response: ([headers: ['Content-Type': 'application/json']] << defaults)
        ])
    }
}