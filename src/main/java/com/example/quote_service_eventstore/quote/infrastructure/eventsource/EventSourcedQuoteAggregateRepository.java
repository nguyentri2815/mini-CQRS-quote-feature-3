package com.example.quote_service_eventstore.quote.infrastructure.eventsource;

import com.example.quote_service_eventstore.common.eventsource.AggregateCommandResult;
import com.example.quote_service_eventstore.common.eventsource.LoadedAggregate;
import com.example.quote_service_eventstore.common.eventstore.EventStore;
import com.example.quote_service_eventstore.common.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.common.outbox.OutboxEventStore;
import com.example.quote_service_eventstore.quote.application.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.quote.application.repository.QuoteAggregateRepository;
import com.example.quote_service_eventstore.quote.domain.aggregate.QuoteAggregate;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.quote.service.QuoteAggregateLoader;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventSourcedQuoteAggregateRepository implements QuoteAggregateRepository {

    private static final String QUOTE_AGGREGATE_TYPE = "Quote";

    private final EventStore eventStore;
    private final OutboxEventStore outboxEventStore;
    private final QuoteAggregateLoader quoteAggregateLoader;

    public EventSourcedQuoteAggregateRepository(
            EventStore eventStore,
            OutboxEventStore outboxEventStore,
            QuoteAggregateLoader quoteAggregateLoader
    ) {
        this.eventStore = eventStore;
        this.outboxEventStore = outboxEventStore;
        this.quoteAggregateLoader = quoteAggregateLoader;
    }

    @Override
    public AggregateCommandResult<QuoteAggregate> create(CreateQuoteCommand command) {
        QuoteAggregate aggregate = QuoteAggregate.empty();

        QuoteCreatedEvent event = aggregate.process(command);

        return commit(
                aggregate,
                event,
                0L
        );
    }

    @Override
    public AggregateCommandResult<QuoteAggregate> update(
            String quoteId,
            SubmitQuoteCommand command
    ) {
        LoadedAggregate<QuoteAggregate> loadedAggregate =
                quoteAggregateLoader.loadWithVersion(quoteId);

        QuoteAggregate aggregate = loadedAggregate.getAggregate();
        long expectedVersion = loadedAggregate.getVersion();

        QuoteSubmittedEvent event = aggregate.process(command);

        return commit(
                aggregate,
                event,
                expectedVersion
        );
    }

    @Override
    public AggregateCommandResult<QuoteAggregate> update(
            String quoteId,
            ApproveQuoteCommand command
    ) {
        LoadedAggregate<QuoteAggregate> loadedAggregate =
                quoteAggregateLoader.loadWithVersion(quoteId);

        QuoteAggregate aggregate = loadedAggregate.getAggregate();
        long expectedVersion = loadedAggregate.getVersion();

        QuoteApprovedEvent event = aggregate.process(command);

        return commit(
                aggregate,
                event,
                expectedVersion
        );
    }

    private AggregateCommandResult<QuoteAggregate> commit(
            QuoteAggregate aggregate,
            DomainEvent event,
            long expectedVersion
    ) {
        EventStoreRecord record = eventStore.append(
                QUOTE_AGGREGATE_TYPE,
                event,
                expectedVersion
        );

        outboxEventStore.save(
                event,
                record.getVersion()
        );

        aggregate.apply(event);

        return new AggregateCommandResult<>(
                aggregate.getId(),
                record.getVersion(),
                aggregate,
                List.of(event)
        );
    }
}
