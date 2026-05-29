package com.example.quote_service_eventstore.command.quote.infrastructure.outbox;

import com.example.quote_service_eventstore.shared.eventstore.EventSerializer;
import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import com.example.quote_service_eventstore.shared.observability.CorrelationIdProvider;
import com.example.quote_service_eventstore.shared.outbox.OutboxEventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OutboxEventStore {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventStore.class);
    private static final String QUOTE_AGGREGATE_TYPE = "Quote";

    private final OutboxEventRepository outboxEventRepository;
    private final EventSerializer eventSerializer;
    private final CorrelationIdProvider correlationIdProvider;

    public OutboxEventStore(
            OutboxEventRepository outboxEventRepository,
            EventSerializer eventSerializer,
            CorrelationIdProvider correlationIdProvider
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.eventSerializer = eventSerializer;
        this.correlationIdProvider = correlationIdProvider;
    }

    public void save(DomainEvent event, long aggregateVersion) {
        String correlationId = correlationIdProvider.getCorrelationId();

        log.info(
                "[OUTBOX_STORE] Save requested. eventType={}, aggregateId={}, version={}, correlationId={}",
                event.eventName(),
                event.aggregateId(),
                aggregateVersion,
                correlationId
        );

        OutboxEventEntity entity = new OutboxEventEntity(
                UUID.randomUUID().toString(),
                event.aggregateId(),
                QUOTE_AGGREGATE_TYPE,
                event.eventName(),
                eventSerializer.serialize(event),
                correlationId,
                aggregateVersion,
                OutboxEventStatus.PENDING,
                0,
                null,
                LocalDateTime.now(),
                null
        );

        try {
            outboxEventRepository.save(entity);
        } catch (RuntimeException exception) {
            log.error(
                    "[OUTBOX_STORE] Save failed. outboxId={}, eventType={}, aggregateId={}, version={}, correlationId={}",
                    entity.getId(),
                    entity.getEventType(),
                    entity.getAggregateId(),
                    entity.getAggregateVersion(),
                    correlationId,
                    exception
            );
            throw exception;
        }

        log.info(
                "[OUTBOX_STORE] Saved pending event. outboxId={}, eventType={}, aggregateId={}, version={}, correlationId={}",
                entity.getId(),
                entity.getEventType(),
                entity.getAggregateId(),
                entity.getAggregateVersion(),
                correlationId
        );
    }
}
