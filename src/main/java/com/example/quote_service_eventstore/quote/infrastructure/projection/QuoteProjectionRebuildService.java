package com.example.quote_service_eventstore.quote.infrastructure.projection;

import com.example.quote_service_eventstore.common.eventstore.EventDeserializer;
import com.example.quote_service_eventstore.common.eventstore.EventStore;
import com.example.quote_service_eventstore.common.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import com.example.quote_service_eventstore.quote.infrastructure.projection.handler.QuoteStateProjectionHandler;
import com.example.quote_service_eventstore.quote.infrastructure.projection.repository.QuoteStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuoteProjectionRebuildService {

    private final EventStore eventStore;
    private final EventDeserializer eventDeserializer;
    private final QuoteStateProjectionHandler quoteStateProjectionHandler;
    private final QuoteStateRepository quoteStateRepository;

    public QuoteProjectionRebuildService(
            EventStore eventStore,
            EventDeserializer eventDeserializer,
            QuoteStateProjectionHandler quoteStateProjectionHandler,
            QuoteStateRepository quoteStateRepository
    ) {
        this.eventStore = eventStore;
        this.eventDeserializer = eventDeserializer;
        this.quoteStateProjectionHandler = quoteStateProjectionHandler;
        this.quoteStateRepository = quoteStateRepository;
    }

    @Transactional
    public void rebuildAll() {
        quoteStateRepository.deleteAll();

        for (EventStoreRecord record : eventStore.findAll()) {
            DomainEvent event = eventDeserializer.deserialize(record);

            quoteStateProjectionHandler.project(
                    event,
                    record.getVersion()
            );
        }
    }

    @Transactional
    public void rebuildOne(String quoteId) {
        quoteStateRepository.deleteById(quoteId);

        for (EventStoreRecord record : eventStore.findByAggregateId(quoteId)) {
            DomainEvent event = eventDeserializer.deserialize(record);

            quoteStateProjectionHandler.project(
                    event,
                    record.getVersion()
            );
        }
    }

}
