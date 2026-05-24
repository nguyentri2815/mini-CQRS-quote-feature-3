package com.example.quote_service_eventstore.common.eventstore;

import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;

import java.util.List;

public interface EventStore {

    EventStoreRecord append(String aggregateType, DomainEvent event);

    EventStoreRecord append(
            String aggregateType,
            DomainEvent event,
            long expectedVersion
    );

    List<EventStoreRecord> findByAggregateId(String aggregateId);

    List<EventStoreRecord> findAll();
}
