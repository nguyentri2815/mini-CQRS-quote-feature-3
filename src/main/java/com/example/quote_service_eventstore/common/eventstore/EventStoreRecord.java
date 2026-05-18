package com.example.quote_service_eventstore.common.eventstore;

import java.time.LocalDateTime;

public class EventStoreRecord {

    private final String eventId;
    private final String aggregateId;
    private final String aggregateType;
    private final String eventType;
    private final String payload;
    private final long version;
    private final LocalDateTime createdAt;

    public EventStoreRecord(
            String eventId,
            String aggregateId,
            String aggregateType,
            String eventType,
            String payload,
            long version,
            LocalDateTime createdAt
    ) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.version = version;
        this.createdAt = createdAt;
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

    public long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
