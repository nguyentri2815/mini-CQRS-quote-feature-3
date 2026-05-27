package com.example.quote_service_eventstore.flow.quote.consumer;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.shared.eventstore.EventDeserializer;
import com.example.quote_service_eventstore.shared.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.DomainEventRabbitConfig;
import com.example.quote_service_eventstore.shared.messaging.dedup.MessageDedupService;
import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
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

        for (DomainEventHandler<? extends DomainEvent> handler : handlers) {
            if (handler.eventType().equals(event.getClass())) {
                dispatch(handler, event, message);
            }
        }
        messageDedupService.markProcessed(message);
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

        typedHandler.handle(envelope);
    }
}
