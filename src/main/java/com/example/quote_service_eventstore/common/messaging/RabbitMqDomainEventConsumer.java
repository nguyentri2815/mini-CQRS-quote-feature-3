package com.example.quote_service_eventstore.common.messaging;

import com.example.quote_service_eventstore.common.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.common.eventstore.EventDeserializer;
import com.example.quote_service_eventstore.common.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.common.messaging.dedup.MessageDedupService;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
    public void consume(DomainEventMessage message) {
        log.info(
                "[RABBIT_CONSUMER] Consuming event message. messageId={}, eventType={}, aggregateId={}",
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId()
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
                0L,
                message.getOccurredAt()
        );

        DomainEvent event = eventDeserializer.deserialize(temporaryRecord);

        for (DomainEventHandler<? extends DomainEvent> handler : handlers) {
            if (handler.eventType().equals(event.getClass())) {
                dispatch(handler, event);
            }
        }
        messageDedupService.markProcessed(message);
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent> void dispatch(
            DomainEventHandler<? extends DomainEvent> handler,
            DomainEvent event
    ) {
        DomainEventHandler<T> typedHandler = (DomainEventHandler<T>) handler;
        typedHandler.handle((T) event);
    }
}
