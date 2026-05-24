package com.example.quote_service_eventstore.common.eventsource;

import com.example.quote_service_eventstore.common.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;

public class EventAppendResult {

    private final String aggregateId;
    private final long oldVersion;
    private final long newVersion;
    private final DomainEvent event;
    private final EventStoreRecord eventStoreRecord;

    public EventAppendResult(
            String aggregateId,
            long oldVersion,
            long newVersion,
            DomainEvent event,
            EventStoreRecord eventStoreRecord
    ) {
        this.aggregateId = aggregateId;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.event = event;
        this.eventStoreRecord = eventStoreRecord;
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

    public DomainEvent getEvent() {
        return event;
    }

    public EventStoreRecord getEventStoreRecord() {
        return eventStoreRecord;
    }

    public String getEventName() {
        return event.eventName();
    }
}
