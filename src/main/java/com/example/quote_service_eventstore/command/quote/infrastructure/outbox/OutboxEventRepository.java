package com.example.quote_service_eventstore.command.quote.infrastructure.outbox;

import com.example.quote_service_eventstore.shared.outbox.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, String> {

    List<OutboxEventEntity> findTop50ByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status
    );

    List<OutboxEventEntity> findByAggregateIdOrderByCreatedAtAsc(String aggregateId);
}
