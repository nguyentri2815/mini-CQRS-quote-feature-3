package com.example.quote_service_eventstore.flow.quote.consumer;

import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.shared.eventstore.EventDeserializer;
import com.example.quote_service_eventstore.shared.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.dedup.MessageDedupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainEventMessageProcessor {

    private static final Logger log = LoggerFactory.getLogger(DomainEventMessageProcessor.class);

    private final EventDeserializer eventDeserializer;
    private final List<DomainEventHandler<? extends DomainEvent>> handlers;
    private final MessageDedupService messageDedupService;

    public DomainEventMessageProcessor(
            EventDeserializer eventDeserializer,
            List<DomainEventHandler<? extends DomainEvent>> handlers,
            MessageDedupService messageDedupService
    ) {
        this.eventDeserializer = eventDeserializer;
        this.handlers = handlers;
        this.messageDedupService = messageDedupService;
    }

    public void process(DomainEventMessage message) {
        if (messageDedupService.isProcessed(message.getEventId())) {
            log.info(
                    "[EVENT_PROCESSOR] Message already processed. Skip. messageId={}, eventType={}, aggregateId={}",
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

        dispatchToHandlers(event, message);

        messageDedupService.markProcessed(message);
    }

    private void dispatchToHandlers(
            DomainEvent event,
            DomainEventMessage message
    ) {
        for (DomainEventHandler<? extends DomainEvent> handler : handlers) {
            if (handler.eventType().equals(event.getClass())) {
                dispatch(handler, event, message);
            }
        }
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
