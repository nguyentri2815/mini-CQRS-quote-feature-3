package com.example.quote_service_eventstore.common.outbox;

import com.example.quote_service_eventstore.common.eventstore.EventSerializer;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OutboxEventStore {

    private static final String QUOTE_AGGREGATE_TYPE = "Quote";

    private final OutboxEventRepository outboxEventRepository;
    private final EventSerializer eventSerializer;

    public OutboxEventStore(
            OutboxEventRepository outboxEventRepository,
            EventSerializer eventSerializer
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.eventSerializer = eventSerializer;
    }

    public void save(DomainEvent event) {
        OutboxEventEntity entity = new OutboxEventEntity(
                UUID.randomUUID().toString(),
                event.aggregateId(),
                QUOTE_AGGREGATE_TYPE,
                event.eventName(),
                eventSerializer.serialize(event),
                OutboxEventStatus.PENDING,
                0,
                null,
                LocalDateTime.now(),
                null
        );

        outboxEventRepository.save(entity);
    }
}
