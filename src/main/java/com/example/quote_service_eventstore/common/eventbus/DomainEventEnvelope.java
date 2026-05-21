package com.example.quote_service_eventstore.common.eventbus;

import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;

public class DomainEventEnvelope<T extends DomainEvent> {

    private final String messageId;
    private final T event;
    private final long aggregateVersion;

    public DomainEventEnvelope(
            String messageId,
            T event,
            long aggregateVersion
    ) {
        this.messageId = messageId;
        this.event = event;
        this.aggregateVersion = aggregateVersion;
    }

    public String getMessageId() {
        return messageId;
    }

    public T getEvent() {
        return event;
    }

    public long getAggregateVersion() {
        return aggregateVersion;
    }
}
