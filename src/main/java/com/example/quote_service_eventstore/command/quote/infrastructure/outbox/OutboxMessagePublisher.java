package com.example.quote_service_eventstore.command.quote.infrastructure.outbox;

import com.example.quote_service_eventstore.command.quote.infrastructure.kafka.KafkaDomainEventPublisher;
import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.DomainEventRabbitConfig;
import com.example.quote_service_eventstore.shared.observability.ObservabilityConstants;
import com.example.quote_service_eventstore.shared.outbox.OutboxEventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private final KafkaDomainEventPublisher kafkaDomainEventPublisher;

    public OutboxMessagePublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaDomainEventPublisher kafkaDomainEventPublisher
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaDomainEventPublisher = kafkaDomainEventPublisher;
    }

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
            DomainEventMessage message = toMessage(outboxEvent);

            kafkaDomainEventPublisher.publish(message);

            outboxEvent.markSent(LocalDateTime.now());

            outboxEventRepository.save(outboxEvent);

            log.info(
                    "[OUTBOX] Published event to Kafka. outboxId={}, eventType={}, aggregateId={}, version={}, correlationId={}",
                    outboxEvent.getId(),
                    outboxEvent.getEventType(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getAggregateVersion(),
                    outboxEvent.getCorrelationId()
            );
        } catch (Exception exception) {
            outboxEvent.markFailed(exception.getMessage());
            outboxEventRepository.save(outboxEvent);

            log.error(
                    "[OUTBOX] Failed to publish event to Kafka. outboxId={}, eventType={}, aggregateId={}, version={}",
                    outboxEvent.getId(),
                    outboxEvent.getEventType(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getAggregateVersion(),
                    exception
            );
        }
    }

    private DomainEventMessage toMessage(OutboxEventEntity entity) {
        return new DomainEventMessage(
                entity.getId(),
                entity.getAggregateId(),
                entity.getAggregateType(),
                entity.getEventType(),
                entity.getPayload(),
                entity.getAggregateVersion(),
                entity.getCorrelationId(),
                entity.getCreatedAt()
        );
    }
}
