package com.example.quote_service_eventstore.shared.messaging;

import java.time.LocalDateTime;

public class DomainEventMessage {

    private String eventId;
    private String aggregateId;
    private String aggregateType;
    private String eventType;
    private String payload;
    private long aggregateVersion;
    private LocalDateTime occurredAt;
    private String correlationId;

    public DomainEventMessage() {
    }

    public DomainEventMessage(
            String eventId,
            String aggregateId,
            String aggregateType,
            String eventType,
            String payload,
            long aggregateVersion,
            String correlationId,
            LocalDateTime occurredAt
    ) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.aggregateVersion = aggregateVersion;
        this.correlationId = correlationId;
        this.occurredAt = occurredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public long getAggregateVersion() {
        return aggregateVersion;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

}
