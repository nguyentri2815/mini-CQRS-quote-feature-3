package com.example.quote_service_eventstore.admin.quote.dto;

import java.time.LocalDateTime;

public class EventTimelineItem {

    private final String eventId;
    private final String aggregateId;
    private final String aggregateType;
    private final String eventType;
    private final long version;
    private final LocalDateTime createdAt;

    public EventTimelineItem(
            String eventId,
            String aggregateId,
            String aggregateType,
            String eventType,
            long version,
            LocalDateTime createdAt
    ) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
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

    public long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
