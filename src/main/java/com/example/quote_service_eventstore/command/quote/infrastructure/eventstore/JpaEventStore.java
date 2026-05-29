package com.example.quote_service_eventstore.command.quote.infrastructure.eventstore;

import com.example.quote_service_eventstore.shared.eventstore.EventSerializer;
import com.example.quote_service_eventstore.shared.eventstore.EventStore;
import com.example.quote_service_eventstore.shared.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.shared.exception.ConcurrencyException;
import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Primary
public class JpaEventStore implements EventStore {

    private static final Logger log = LoggerFactory.getLogger(JpaEventStore.class);

    private final EventStoreJpaRepository eventStoreJpaRepository;
    private final EventSerializer eventSerializer;

    public JpaEventStore(
            EventStoreJpaRepository eventStoreJpaRepository,
            EventSerializer eventSerializer
    ) {
        this.eventStoreJpaRepository = eventStoreJpaRepository;
        this.eventSerializer = eventSerializer;
    }

    @Override
    public EventStoreRecord append(String aggregateType, DomainEvent event) {
        long expectedVersion = currentVersion(event.aggregateId());

        return append(
                aggregateType,
                event,
                expectedVersion
        );
    }

    @Override
    public EventStoreRecord append(
            String aggregateType,
            DomainEvent event,
            long expectedVersion
    ) {
        long currentVersion = currentVersion(event.aggregateId());

        log.info(
                "[EVENT_STORE] Append requested. eventType={}, aggregateType={}, aggregateId={}, expectedVersion={}, currentVersion={}",
                event.eventName(),
                aggregateType,
                event.aggregateId(),
                expectedVersion,
                currentVersion
        );

        if (currentVersion != expectedVersion) {
            log.warn(
                    "[EVENT_STORE] Version conflict before append. eventType={}, aggregateId={}, expectedVersion={}, currentVersion={}",
                    event.eventName(),
                    event.aggregateId(),
                    expectedVersion,
                    currentVersion
            );

            throw new ConcurrencyException(
                    "Aggregate version conflict. aggregateId=" + event.aggregateId()
                            + ", expectedVersion=" + expectedVersion
                            + ", currentVersion=" + currentVersion
            );
        }

        long nextVersion = expectedVersion + 1;

        EventStoreEntity entity = new EventStoreEntity(
                UUID.randomUUID().toString(),
                event.aggregateId(),
                aggregateType,
                event.eventName(),
                eventSerializer.serialize(event),
                nextVersion,
                LocalDateTime.now()
        );

        try {
            EventStoreEntity savedEntity = eventStoreJpaRepository.saveAndFlush(entity);

            log.info(
                    "[EVENT_STORE] Append committed. eventId={}, eventType={}, aggregateType={}, aggregateId={}, version={}",
                    savedEntity.getEventId(),
                    savedEntity.getEventType(),
                    savedEntity.getAggregateType(),
                    savedEntity.getAggregateId(),
                    savedEntity.getVersion()
            );

            return toRecord(savedEntity);
        } catch (DataIntegrityViolationException exception) {
            log.error(
                    "[EVENT_STORE] Append failed because of data integrity violation. eventType={}, aggregateId={}, version={}",
                    event.eventName(),
                    event.aggregateId(),
                    nextVersion,
                    exception
            );

            throw new ConcurrencyException(
                    "Aggregate version conflict while appending event. aggregateId="
                            + event.aggregateId()
                            + ", version=" + nextVersion,
                    exception
            );
        }
    }

    private long currentVersion(String aggregateId) {
        return eventStoreJpaRepository.findMaxVersionByAggregateId(aggregateId)
                .orElse(0L);
    }


    @Override
    public List<EventStoreRecord> findByAggregateId(String aggregateId) {
        List<EventStoreRecord> records = eventStoreJpaRepository.findByAggregateIdOrderByVersionAsc(aggregateId)
                .stream()
                .map(this::toRecord)
                .toList();

        log.info(
                "[EVENT_STORE] Loaded aggregate events. aggregateId={}, recordCount={}",
                aggregateId,
                records.size()
        );

        return records;
    }

    @Override
    public List<EventStoreRecord> findAll() {
        List<EventStoreRecord> records = eventStoreJpaRepository.findAllByOrderByCreatedAtAsc()
                .stream()
                .map(this::toRecord)
                .toList();

        log.info("[EVENT_STORE] Loaded all events. recordCount={}", records.size());

        return records;
    }

    private long nextVersion(String aggregateId) {
        return eventStoreJpaRepository.countByAggregateId(aggregateId) + 1;
    }

    private EventStoreRecord toRecord(EventStoreEntity entity) {
        return new EventStoreRecord(
                entity.getEventId(),
                entity.getAggregateId(),
                entity.getAggregateType(),
                entity.getEventType(),
                entity.getPayload(),
                entity.getVersion(),
                entity.getCreatedAt()
        );
    }
}
