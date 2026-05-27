package com.example.quote_service_eventstore.domain.quote.event;

import java.time.LocalDateTime;

public interface DomainEvent {

    String eventName();

    String aggregateId();

    LocalDateTime occurredAt();

}
