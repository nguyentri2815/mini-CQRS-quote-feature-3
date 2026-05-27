package com.example.quote_service_eventstore.shared.eventbus;

import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
