package com.example.quote_service_eventstore.common.outbox;

public enum OutboxEventStatus {
    PENDING,
    SENT,
    FAILED
}
