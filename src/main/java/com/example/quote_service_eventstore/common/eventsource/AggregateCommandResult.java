package com.example.quote_service_eventstore.common.eventsource;

import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;

import java.util.List;

public class AggregateCommandResult<A> {

    private final String aggregateId;
    private final long oldVersion;
    private final long newVersion;
    private final A aggregate;
    private final List<DomainEvent> events;
    private final List<EventAppendResult> appendResults;

    public AggregateCommandResult(
            String aggregateId,
            long oldVersion,
            long newVersion,
            A aggregate,
            List<DomainEvent> events,
            List<EventAppendResult> appendResults
    ) {
        this.aggregateId = aggregateId;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.aggregate = aggregate;
        this.events = events;
        this.appendResults = appendResults;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public long getOldVersion() {
        return oldVersion;
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

    public List<EventAppendResult> getAppendResults() {
        return appendResults;
    }

    public List<String> getEventNames() {
        return events.stream()
                .map(DomainEvent::eventName)
                .toList();
    }
}
