package com.example.quote_service_eventstore.command.quote.infrastructure.outbox;

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

        if (!pendingEvents.isEmpty()) {
            log.info("[OUTBOX_PUBLISHER] Found pending events. count={}", pendingEvents.size());
        }

        for (OutboxEventEntity outboxEvent : pendingEvents) {
            publishOne(outboxEvent);
        }
    }

    private void publishOne(OutboxEventEntity outboxEvent) {
        String correlationId = outboxEvent.getCorrelationId();

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "unknown";
        }

        MDC.put(
                ObservabilityConstants.CORRELATION_ID_MDC_KEY,
                correlationId
        );

        try {
            DomainEventMessage message = new DomainEventMessage(
                    outboxEvent.getId(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getAggregateType(),
                    outboxEvent.getEventType(),
                    outboxEvent.getPayload(),
                    outboxEvent.getAggregateVersion(),
                    correlationId,
                    outboxEvent.getCreatedAt()
            );

            log.info(
                    "[OUTBOX_PUBLISHER] Publishing event. eventId={}, eventType={}, aggregateId={}, version={}, correlationId={}",
                    outboxEvent.getId(),
                    outboxEvent.getEventType(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getAggregateVersion(),
                    correlationId
            );

            rabbitTemplate.convertAndSend(
                    DomainEventRabbitConfig.DOMAIN_EVENT_EXCHANGE,
                    DomainEventRabbitConfig.QUOTE_EVENT_ROUTING_KEY,
                    message
            );

            outboxEvent.markSent(LocalDateTime.now());

            log.info(
                    "[OUTBOX_PUBLISHER] Published event and marked sent. eventId={}, eventType={}, aggregateId={}, version={}, correlationId={}",
                    outboxEvent.getId(),
                    outboxEvent.getEventType(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getAggregateVersion(),
                    correlationId
            );

        } catch (Exception exception) {
            log.error(
                    "[OUTBOX_PUBLISHER] Failed to publish event. outboxId={}, eventType={}, aggregateId={}, version={}, correlationId={}",
                    outboxEvent.getId(),
                    outboxEvent.getEventType(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getAggregateVersion(),
                    correlationId,
                    exception
            );

            outboxEvent.markFailed(exception.getMessage());

            log.warn(
                    "[OUTBOX_PUBLISHER] Marked event failed. outboxId={}, eventType={}, aggregateId={}, retryCount={}, correlationId={}",
                    outboxEvent.getId(),
                    outboxEvent.getEventType(),
                    outboxEvent.getAggregateId(),
                    outboxEvent.getRetryCount(),
                    correlationId
            );
        } finally {
            MDC.remove(ObservabilityConstants.CORRELATION_ID_MDC_KEY);
        }
    }
}
