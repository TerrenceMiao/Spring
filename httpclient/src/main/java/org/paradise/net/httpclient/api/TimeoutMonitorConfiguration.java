package org.paradise.net.httpclient.api;

public interface TimeoutMonitorConfiguration {

    default int corePoolSize() {
        return 10;
    }

    String namePrefix();
}
