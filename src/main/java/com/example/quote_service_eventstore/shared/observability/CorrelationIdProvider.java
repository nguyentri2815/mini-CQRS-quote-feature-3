package com.example.quote_service_eventstore.shared.observability;

public interface CorrelationIdProvider {

    String getCorrelationId();
}
