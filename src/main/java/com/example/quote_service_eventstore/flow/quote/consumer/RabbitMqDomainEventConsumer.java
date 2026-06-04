package com.example.quote_service_eventstore.flow.quote.consumer;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.shared.eventstore.EventDeserializer;
import com.example.quote_service_eventstore.shared.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.DomainEventRabbitConfig;
import com.example.quote_service_eventstore.shared.messaging.dedup.MessageDedupService;
import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import com.example.quote_service_eventstore.shared.observability.ObservabilityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile("rabbit")
public class RabbitMqDomainEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqDomainEventConsumer.class);

    private final EventDeserializer eventDeserializer;
    private final List<DomainEventHandler<? extends DomainEvent>> handlers;
    private final MessageDedupService messageDedupService;

    public RabbitMqDomainEventConsumer(
            EventDeserializer eventDeserializer,
            List<DomainEventHandler<? extends DomainEvent>> handlers,
            MessageDedupService messageDedupService
    ) {
        this.eventDeserializer = eventDeserializer;
        this.handlers = handlers;
        this.messageDedupService = messageDedupService;
    }

    @RabbitListener(queues = DomainEventRabbitConfig.QUOTE_EVENT_QUEUE)
    @Transactional
    public void consume(DomainEventMessage message) {
        String correlationId = message.getCorrelationId();

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "unknown";
        }

        MDC.put(
                ObservabilityConstants.CORRELATION_ID_MDC_KEY,
                correlationId
        );

        log.info(
                "[CONSUMER] Message received. messageId={}, eventType={}, aggregateId={}, version={}, correlationId={}",
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId(),
                message.getAggregateVersion(),
                correlationId
        );

        try {
            process(message);
        } catch (Exception exception) {
            log.error(
                    "[RABBIT_CONSUMER] Failed to process message. messageId={}, eventType={}, aggregateId={}, version={}",
                    message.getEventId(),
                    message.getEventType(),
                    message.getAggregateId(),
                    message.getAggregateVersion(),
                    exception
            );

            throw exception;
        } finally {
            MDC.remove(ObservabilityConstants.CORRELATION_ID_MDC_KEY);
        }
    }

    private void process(DomainEventMessage message) {
        log.info(
                "[RABBIT_CONSUMER] Consuming event message. messageId={}, eventType={}, aggregateId={}, version={}",
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId(),
                message.getAggregateVersion()
        );

        if (messageDedupService.isProcessed(message.getEventId())) {
            log.warn(
                    "[RABBIT_CONSUMER] Duplicate message skipped. messageId={}, eventType={}, aggregateId={}",
                    message.getEventId(),
                    message.getEventType(),
                    message.getAggregateId()
            );
            return;
        }

        EventStoreRecord temporaryRecord = new EventStoreRecord(
                message.getEventId(),
                message.getAggregateId(),
                message.getAggregateType(),
                message.getEventType(),
                message.getPayload(),
                message.getAggregateVersion(),
                message.getOccurredAt()
        );

        DomainEvent event = eventDeserializer.deserialize(temporaryRecord);

        log.info(
                "[CONSUMER] Message deserialized. messageId={}, eventType={}, aggregateId={}, version={}",
                message.getEventId(),
                event.eventName(),
                event.aggregateId(),
                message.getAggregateVersion()
        );

        int dispatchedHandlerCount = 0;

        for (DomainEventHandler<? extends DomainEvent> handler : handlers) {
            if (handler.eventType().equals(event.getClass())) {
                dispatch(handler, event, message);
                dispatchedHandlerCount++;
            }
        }

        if (dispatchedHandlerCount == 0) {
            log.warn(
                    "[CONSUMER] No handler matched event. messageId={}, eventType={}, aggregateId={}, version={}",
                    message.getEventId(),
                    message.getEventType(),
                    message.getAggregateId(),
                    message.getAggregateVersion()
            );
        }

        messageDedupService.markProcessed(message);

        log.info(
                "[CONSUMER] Processed event message. messageId={}, eventType={}, aggregateId={}, version={}, dispatchedHandlers={}",
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId(),
                message.getAggregateVersion(),
                dispatchedHandlerCount
        );
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent> void dispatch(
            DomainEventHandler<? extends DomainEvent> handler,
            DomainEvent event,
            DomainEventMessage message
    ) {
        DomainEventHandler<T> typedHandler = (DomainEventHandler<T>) handler;
        T typedEvent = (T) event;

        DomainEventEnvelope<T> envelope = new DomainEventEnvelope<>(
                message.getEventId(),
                typedEvent,
                message.getAggregateVersion()
        );

        log.info(
                "[CONSUMER] Dispatching event handler. messageId={}, eventType={}, aggregateId={}, version={}, handler={}",
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId(),
                message.getAggregateVersion(),
                handler.getClass().getSimpleName()
        );

        typedHandler.handle(envelope);

        log.info(
                "[CONSUMER] Event handler completed. messageId={}, eventType={}, aggregateId={}, version={}, handler={}",
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId(),
                message.getAggregateVersion(),
                handler.getClass().getSimpleName()
        );
    }
}
