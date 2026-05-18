package com.example.quote_service_eventstore.common.eventstore.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventStoreJpaRepository extends JpaRepository<EventStoreEntity, String> {

    List<EventStoreEntity> findByAggregateIdOrderByVersionAsc(String aggregateId);

    List<EventStoreEntity> findAllByOrderByCreatedAtAsc();

    long countByAggregateId(String aggregateId);
}
