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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventSourcedQuoteAggregateRepository implements QuoteAggregateRepository {

    private static final Logger log = LoggerFactory.getLogger(EventSourcedQuoteAggregateRepository.class);
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
        log.info(
                "[EVENT_SOURCE] Commit started. eventType={}, aggregateId={}, oldVersion={}",
                event.eventName(),
                event.aggregateId(),
                oldVersion
        );

        EventStoreRecord record = eventStore.append(
                QUOTE_AGGREGATE_TYPE,
                event,
                oldVersion
        );

        log.info(
                "[EVENT_STORE] Appended event. eventId={}, eventType={}, aggregateId={}, oldVersion={}, newVersion={}",
                record.getEventId(),
                record.getEventType(),
                record.getAggregateId(),
                oldVersion,
                record.getVersion()
        );

        outboxEventStore.save(
                event,
                record.getVersion()
        );

        log.info(
                "[EVENT_SOURCE] Outbox saved for committed event. eventId={}, eventType={}, aggregateId={}, version={}",
                record.getEventId(),
                record.getEventType(),
                record.getAggregateId(),
                record.getVersion()
        );

        aggregate.apply(event);

        log.info(
                "[EVENT_SOURCE] Aggregate applied event. eventType={}, aggregateId={}, oldVersion={}, newVersion={}",
                event.eventName(),
                event.aggregateId(),
                oldVersion,
                record.getVersion()
        );

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
