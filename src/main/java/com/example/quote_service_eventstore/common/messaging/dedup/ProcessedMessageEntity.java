package com.example.quote_service_eventstore.common.messaging.dedup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_messages")
public class ProcessedMessageEntity {

    @Id
    @Column(name = "message_id", nullable = false, length = 64)
    private String messageId;

    @Column(name = "event_type", nullable = false, length = 200)
    private String eventType;

    @Column(name = "aggregate_id", nullable = false, length = 64)
    private String aggregateId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    protected ProcessedMessageEntity() {
    }

    public ProcessedMessageEntity(
            String messageId,
            String eventType,
            String aggregateId,
            LocalDateTime processedAt
    ) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.processedAt = processedAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
