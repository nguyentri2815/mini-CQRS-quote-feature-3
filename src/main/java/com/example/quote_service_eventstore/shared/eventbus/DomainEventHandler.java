package com.example.quote_service_eventstore.shared.eventbus;

import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;

public interface DomainEventHandler<T extends DomainEvent> {

    Class<T> eventType();

    void handle(DomainEventEnvelope<T> envelope);
}

