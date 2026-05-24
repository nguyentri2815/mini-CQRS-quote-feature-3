package com.example.quote_service_eventstore.common.eventstore.jpa;

import com.example.quote_service_eventstore.common.eventstore.EventSerializer;
import com.example.quote_service_eventstore.common.eventstore.EventStore;
import com.example.quote_service_eventstore.common.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.common.exception.ConcurrencyException;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Primary
public class JpaEventStore implements EventStore {

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

        if (currentVersion != expectedVersion) {
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
            return toRecord(savedEntity);
        } catch (DataIntegrityViolationException exception) {
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
        return eventStoreJpaRepository.findByAggregateIdOrderByVersionAsc(aggregateId)
                .stream()
                .map(this::toRecord)
                .toList();
    }

    @Override
    public List<EventStoreRecord> findAll() {
        return eventStoreJpaRepository.findAllByOrderByCreatedAtAsc()
                .stream()
                .map(this::toRecord)
                .toList();
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
