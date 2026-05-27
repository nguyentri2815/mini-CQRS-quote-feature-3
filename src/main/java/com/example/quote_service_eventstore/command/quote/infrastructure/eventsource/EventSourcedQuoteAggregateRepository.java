package com.example.quote_service_eventstore.command.quote.infrastructure.eventsource;

import com.example.quote_service_eventstore.shared.eventsource.AggregateCommandResult;
import com.example.quote_service_eventstore.shared.eventsource.EventAppendResult;
import com.example.quote_service_eventstore.shared.eventsource.LoadedAggregate;
import com.example.quote_service_eventstore.shared.eventstore.EventStore;
import com.example.quote_service_eventstore.shared.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.command.quote.infrastructure.outbox.OutboxEventStore;
import com.example.quote_service_eventstore.domain.quote.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.command.quote.application.repository.QuoteAggregateRepository;
import com.example.quote_service_eventstore.domain.quote.aggregate.QuoteAggregate;
import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import com.example.quote_service_eventstore.domain.quote.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.domain.quote.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.domain.quote.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.command.quote.application.service.QuoteAggregateLoader;
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
        long oldVersion = loadedAggregate.getVersion();

        QuoteSubmittedEvent event = aggregate.process(command);

        return commit(
                aggregate,
                event,
                oldVersion
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
        long oldVersion = loadedAggregate.getVersion();

        QuoteApprovedEvent event = aggregate.process(command);

        return commit(
                aggregate,
                event,
                oldVersion
        );
    }

    private AggregateCommandResult<QuoteAggregate> commit(
            QuoteAggregate aggregate,
            DomainEvent event,
            long oldVersion
    ) {
        EventStoreRecord record = eventStore.append(
                QUOTE_AGGREGATE_TYPE,
                event,
                oldVersion
        );

        outboxEventStore.save(
                event,
                record.getVersion()
        );

        aggregate.apply(event);

        EventAppendResult appendResult = new EventAppendResult(
                event.aggregateId(),
                oldVersion,
                record.getVersion(),
                event,
                record
        );

        return new AggregateCommandResult<>(
                aggregate.getId(),
                oldVersion,
                record.getVersion(),
                aggregate,
                List.of(event),
                List.of(appendResult)
        );
    }
}