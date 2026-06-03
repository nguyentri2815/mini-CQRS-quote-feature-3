package com.example.quote_service_eventstore.shared.messaging.dedup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, String> {

    List<ProcessedMessageEntity> findByAggregateIdOrderByProcessedAtAsc(String aggregateId);
}
