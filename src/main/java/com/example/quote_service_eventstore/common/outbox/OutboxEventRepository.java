package com.example.quote_service_eventstore.common.outbox;

import com.example.quote_service_eventstore.common.outbox.OutboxEventEntity;
import com.example.quote_service_eventstore.common.outbox.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, String> {

    List<OutboxEventEntity> findTop50ByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status
    );
}
