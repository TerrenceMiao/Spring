package org.paradise.net.httpclient.exception;

import java.io.IOException;

public class CircuitBreakerException extends IOException {

    public CircuitBreakerException(String message) {
        super(message);
    }
}
