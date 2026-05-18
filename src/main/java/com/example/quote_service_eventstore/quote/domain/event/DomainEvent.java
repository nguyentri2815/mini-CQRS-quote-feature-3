package com.example.quote_service_eventstore.quote.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {

    String eventName();

    String aggregateId();

    LocalDateTime occurredAt();

}
