package com.example.quote_service_eventstore.admin.quote.dto;

import java.time.LocalDateTime;

public class ProcessedMessageDebugItem {

    private final String messageId;
    private final String aggregateId;
    private final String eventType;
    private final LocalDateTime processedAt;

    public ProcessedMessageDebugItem(
            String messageId,
            String aggregateId,
            String eventType,
            LocalDateTime processedAt
    ) {
        this.messageId = messageId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.processedAt = processedAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
