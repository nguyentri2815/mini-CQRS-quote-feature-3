package com.example.quote_service_eventstore.common.eventbus;

import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;

public interface DomainEventHandler<T extends DomainEvent> {

    Class<T> eventType();

    void handle(T event);
}
