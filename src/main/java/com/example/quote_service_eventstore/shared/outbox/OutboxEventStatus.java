package com.example.quote_service_eventstore.shared.outbox;

public enum OutboxEventStatus {
    PENDING,
    SENT,
    FAILED
}
