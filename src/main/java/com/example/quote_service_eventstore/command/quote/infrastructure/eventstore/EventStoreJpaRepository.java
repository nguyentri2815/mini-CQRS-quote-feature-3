package com.example.quote_service_eventstore.command.quote.infrastructure.eventstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventStoreJpaRepository extends JpaRepository<EventStoreEntity, String> {

    List<EventStoreEntity> findByAggregateIdOrderByVersionAsc(String aggregateId);

    List<EventStoreEntity> findAllByOrderByCreatedAtAsc();

    long countByAggregateId(String aggregateId);

    @Query("select max(e.version) from EventStoreEntity e where e.aggregateId = :aggregateId")
    Optional<Long> findMaxVersionByAggregateId(String aggregateId);

}
