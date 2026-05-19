package com.example.quote_service_eventstore.common.messaging;

import java.time.LocalDateTime;

public class DomainEventMessage {

    private String eventId;
    private String aggregateId;
    private String aggregateType;
    private String eventType;
    private String payload;
    private LocalDateTime occurredAt;

    public DomainEventMessage() {
    }

    public DomainEventMessage(
            String eventId,
            String aggregateId,
            String aggregateType,
            String eventType,
            String payload,
            LocalDateTime occurredAt
    ) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
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
}
