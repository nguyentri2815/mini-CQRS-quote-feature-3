package com.example.quote_service_eventstore.common.eventsource;

import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;

import java.util.List;

public class AggregateCommandResult<A> {

    private final String aggregateId;
    private final long newVersion;
    private final A aggregate;
    private final List<DomainEvent> events;

    public AggregateCommandResult(
            String aggregateId,
            long newVersion,
            A aggregate,
            List<DomainEvent> events
    ) {
        this.aggregateId = aggregateId;
        this.newVersion = newVersion;
        this.aggregate = aggregate;
        this.events = events;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public long getNewVersion() {
        return newVersion;
    }

    public A getAggregate() {
        return aggregate;
    }

    public List<DomainEvent> getEvents() {
        return events;
    }
}
