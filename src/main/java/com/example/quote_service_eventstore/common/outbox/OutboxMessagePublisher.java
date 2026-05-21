package com.example.quote_service_eventstore.common.outbox;

import com.example.quote_service_eventstore.common.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.common.messaging.DomainEventRabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxMessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxMessagePublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxMessagePublisher(
            OutboxEventRepository outboxEventRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventEntity> pendingEvents =
                outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING
                );

        for (OutboxEventEntity outboxEvent : pendingEvents) {
            publishOne(outboxEvent);
        }
    }

    private void publishOne(OutboxEventEntity outboxEvent) {
        try {
            DomainEventMessage message = new DomainEventMessage(
                    outboxEvent.getId(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getAggregateType(),
                    outboxEvent.getEventType(),
                    outboxEvent.getPayload(),
                    outboxEvent.getAggregateVersion(),
                    outboxEvent.getCreatedAt()
            );

            log.info(
                    "[OUTBOX] Publishing message. outboxId={}, eventType={}, aggregateId={}",
                    outboxEvent.getId(),
                    outboxEvent.getEventType(),
                    outboxEvent.getAggregateId()
            );

            rabbitTemplate.convertAndSend(
                    DomainEventRabbitConfig.DOMAIN_EVENT_EXCHANGE,
                    DomainEventRabbitConfig.QUOTE_EVENT_ROUTING_KEY,
                    message
            );

            outboxEvent.markSent(LocalDateTime.now());

        } catch (Exception exception) {
            log.error(
                    "[OUTBOX] Failed to publish message. outboxId={}, eventType={}",
                    outboxEvent.getId(),
                    outboxEvent.getEventType(),
                    exception
            );

            outboxEvent.markFailed(exception.getMessage());
        }
    }
}
