package com.example.quote_service_eventstore.common.messaging.dedup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, String> {
}
