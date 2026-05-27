package com.example.quote_service_eventstore.command.quote.application.service;

import com.example.quote_service_eventstore.shared.eventsource.LoadedAggregate;
import com.example.quote_service_eventstore.shared.eventstore.EventDeserializer;
import com.example.quote_service_eventstore.shared.eventstore.EventStore;
import com.example.quote_service_eventstore.shared.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.shared.exception.NotFoundException;
import com.example.quote_service_eventstore.domain.quote.aggregate.QuoteAggregate;
import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
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
        return loadWithVersion(quoteId).getAggregate();
    }

    public LoadedAggregate<QuoteAggregate> loadWithVersion(String quoteId) {
        List<EventStoreRecord> records = eventStore.findByAggregateId(quoteId);

        if (records.isEmpty()) {
            throw new NotFoundException("Quote not found: " + quoteId);
        }

        QuoteAggregate aggregate = QuoteAggregate.empty();

        long currentVersion = 0L;

        for (EventStoreRecord record : records) {
            DomainEvent event = eventDeserializer.deserialize(record);
            aggregate.apply(event);
            currentVersion = record.getVersion();
        }

        return new LoadedAggregate<>(
                aggregate,
                currentVersion
        );
    }
}
