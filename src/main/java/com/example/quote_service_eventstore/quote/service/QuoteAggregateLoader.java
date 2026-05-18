package com.example.quote_service_eventstore.quote.service;

import com.example.quote_service_eventstore.common.eventstore.EventDeserializer;
import com.example.quote_service_eventstore.common.eventstore.EventStore;
import com.example.quote_service_eventstore.common.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.common.exception.NotFoundException;
import com.example.quote_service_eventstore.quote.domain.aggregate.QuoteAggregate;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuoteAggregateLoader {

    private final EventStore eventStore;
    private final EventDeserializer eventDeserializer;

    public QuoteAggregateLoader(
            EventStore eventStore,
            EventDeserializer eventDeserializer
    ) {
        this.eventStore = eventStore;
        this.eventDeserializer = eventDeserializer;
    }

    public QuoteAggregate load(String quoteId) {
        List<EventStoreRecord> records = eventStore.findByAggregateId(quoteId);

        if (records.isEmpty()) {
            throw new NotFoundException("Quote not found: " + quoteId);
        }

        QuoteAggregate aggregate = QuoteAggregate.empty();

        for (EventStoreRecord record : records) {
            DomainEvent event = eventDeserializer.deserialize(record);
            aggregate.apply(event);
        }

        return aggregate;
    }
}
