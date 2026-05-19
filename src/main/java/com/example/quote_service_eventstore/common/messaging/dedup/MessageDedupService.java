package com.example.quote_service_eventstore.common.messaging.dedup;

import com.example.quote_service_eventstore.common.messaging.DomainEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MessageDedupService {

    private static final Logger log = LoggerFactory.getLogger(MessageDedupService.class);

    private final ProcessedMessageRepository processedMessageRepository;

    public MessageDedupService(ProcessedMessageRepository processedMessageRepository) {
        this.processedMessageRepository = processedMessageRepository;
    }

    @Transactional(readOnly = true)
    public boolean isProcessed(String messageId) {
        return processedMessageRepository.existsById(messageId);
    }

    @Transactional
    public void markProcessed(DomainEventMessage message) {
        ProcessedMessageEntity entity = new ProcessedMessageEntity(
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId(),
                LocalDateTime.now()
        );

        processedMessageRepository.save(entity);

        log.info(
                "[DEDUP] Marked message as processed. messageId={}, eventType={}, aggregateId={}",
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId()
        );
    }
}
