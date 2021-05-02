package org.paradise.net.httpclient.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.paradise.net.httpclient.exception.CircuitBreakerException;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class CircuitBreakerInterceptor implements Interceptor {

    private static final int UNAVAILABLE = 503;

    private enum State {
        CLOSED, OPEN, HALF_OPEN
    }

    private final int failureThreshold;
    private final Duration sleepWindow;
    private final List<Pattern> whitelist;

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicLong circuitOpened = new AtomicLong(-1);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    public CircuitBreakerInterceptor(int failureThreshold, Duration sleepWindow) {
        this(failureThreshold, sleepWindow, null);
    }

    public CircuitBreakerInterceptor(int failureThreshold, Duration sleepWindow, String whitelist) {
        this.failureThreshold = failureThreshold;
        this.sleepWindow = sleepWindow;
        this.whitelist = whitelist != null ? Arrays.stream(whitelist.split(","))
                .map(Pattern::compile)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        // if the URL is whitelisted then bypass circuit breaker logic
        final Request request = chain.request();

        if (isWhitelisted(request)) {
            return chain.proceed(request);
        }

        // if the circuit breaker is open throw an exception
        if (!attemptExecution()) {
            throw new CircuitBreakerException("Circuit breaker is open");
        }

        return process(chain);
    }

    public boolean isWhitelisted(Request request) {
        return whitelist.stream().anyMatch(pattern -> pattern.matcher(request.url().encodedPath()).find());
    }

    private Response process(Chain chain) throws IOException {

        try {
            final Response response = chain.proceed(chain.request());

            if (response.code() == UNAVAILABLE) {
                recordFailure();
                return response;
            }

            recordSuccess();

            return response;
        } catch (IOException e) {
            recordFailure();
            throw e;
        }
    }

    /**
     * Return whether a request is allowed to be made. It is idempotent and does not modify
     * any internal state.
     *
     * @return boolean whether a request should be permitted
     */
    public boolean allowRequest() {
        return state.get() == State.CLOSED || !state.get().equals(State.HALF_OPEN) && isAfterSleepWindow();
    }

    private void recordFailure() {

        if (failureCount.incrementAndGet() >= failureThreshold
                && (state.compareAndSet(State.CLOSED, State.OPEN) || state.compareAndSet(State.HALF_OPEN, State.OPEN))) {
            circuitOpened.set(currentTimeMillis());
        }
    }

    private void recordSuccess() {

        if (state.compareAndSet(State.HALF_OPEN, State.CLOSED)) {
            circuitOpened.set(-1L);
            failureCount.set(0);
        }
    }

    /**
     * Return whether a request is allowed to be made. It is not idempotent and does modify
     * internal state.
     *
     * @return boolean whether a request should be permitted
     */
    private boolean attemptExecution() {
        // only the first request after sleep window should execute
        // if the request succeeds, the state will transition to CLOSED
        // if the request fails, the state will transition to OPEN
        return state.get() == State.CLOSED || isAfterSleepWindow() && state.compareAndSet(State.OPEN, State.HALF_OPEN);
    }

    private boolean isAfterSleepWindow() {
        return currentTimeMillis() > circuitOpened.get() + sleepWindow.toMillis();
    }
}
