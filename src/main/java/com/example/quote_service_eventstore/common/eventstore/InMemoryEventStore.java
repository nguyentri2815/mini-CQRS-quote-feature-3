package com.example.quote_service_eventstore.common.eventstore;

import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemoryEventStore implements EventStore {

    private final List<EventStoreRecord> records = new CopyOnWriteArrayList<>();

    private final EventSerializer eventSerializer;

    public InMemoryEventStore(EventSerializer eventSerializer) {
        this.eventSerializer = eventSerializer;
    }

    @Override
    public EventStoreRecord append(String aggregateType, DomainEvent event) {
        long nextVersion = nextVersion(event.aggregateId());

        EventStoreRecord record = new EventStoreRecord(
                UUID.randomUUID().toString(),
                event.aggregateId(),
                aggregateType,
                event.eventName(),
                eventSerializer.serialize(event),
                nextVersion,
                LocalDateTime.now()
        );

        records.add(record);

        return record;
    }

    @Override
    public List<EventStoreRecord> findByAggregateId(String aggregateId) {
        return records.stream()
                .filter(record -> record.getAggregateId().equals(aggregateId))
                .sorted(Comparator.comparingLong(EventStoreRecord::getVersion))
                .toList();
    }

    @Override
    public List<EventStoreRecord> findAll() {
        return new ArrayList<>(records);
    }

    private long nextVersion(String aggregateId) {
        return records.stream()
                .filter(record -> record.getAggregateId().equals(aggregateId))
                .mapToLong(EventStoreRecord::getVersion)
                .max()
                .orElse(0L) + 1;
    }
}
