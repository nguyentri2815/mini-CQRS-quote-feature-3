package com.example.quote_service_eventstore.shared.observability;

public final class ObservabilityConstants {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    private ObservabilityConstants() {
    }
}
