package com.example.quote_service_eventstore.admin.quote.dto;

import java.time.LocalDateTime;

public class OutboxDebugItem {

    private final String id;
    private final String aggregateId;
    private final String eventType;
    private final long aggregateVersion;
    private final String status;
    private final int retryCount;
    private final String lastError;
    private final String correlationId;
    private final LocalDateTime createdAt;
    private final LocalDateTime sentAt;

    public OutboxDebugItem(
            String id,
            String aggregateId,
            String eventType,
            long aggregateVersion,
            String status,
            int retryCount,
            String lastError,
            String correlationId,
            LocalDateTime createdAt,
            LocalDateTime sentAt
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.aggregateVersion = aggregateVersion;
        this.status = status;
        this.retryCount = retryCount;
        this.lastError = lastError;
        this.correlationId = correlationId;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public String getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public long getAggregateVersion() {
        return aggregateVersion;
    }

    public String getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}
