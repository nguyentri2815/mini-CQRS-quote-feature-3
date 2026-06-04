package com.example.quote_service_eventstore.flow.quote.consumer.kafka;

import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.shared.eventstore.EventDeserializer;
import com.example.quote_service_eventstore.shared.eventstore.EventStoreRecord;
import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.dedup.MessageDedupService;
import com.example.quote_service_eventstore.shared.messaging.kafka.QuoteKafkaTopicNames;
import com.example.quote_service_eventstore.shared.observability.ObservabilityConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Component
public class QuoteKafkaConsumerv1 {

    private static final Logger log = LoggerFactory.getLogger(QuoteKafkaConsumerv1.class);

    private final EventDeserializer eventDeserializer;
    private final List<DomainEventHandler<? extends DomainEvent>> handlers;
    private final MessageDedupService messageDedupService;

    public QuoteKafkaConsumerv1(
            EventDeserializer eventDeserializer,
            List<DomainEventHandler<? extends DomainEvent>> handlers,
            MessageDedupService messageDedupService
    ) {
        this.eventDeserializer = eventDeserializer;
        this.handlers = handlers;
        this.messageDedupService = messageDedupService;
    }

    @KafkaListener(
            topics = QuoteKafkaTopicNames.QUOTE_EVENTS,
            groupId = "quote-flow-service"
    )
    @Transactional
    public void consume(
            DomainEventMessage message,
            ConsumerRecord<String, DomainEventMessage> record
    ) {
        String correlationId = resolveCorrelationId(message);

        MDC.put(
                ObservabilityConstants.CORRELATION_ID_MDC_KEY,
                correlationId
        );

        try {
            log.info(
                    "[KAFKA_CONSUMER] Consuming event. topic={}, partition={}, offset={}, key={}, messageId={}, eventType={}, aggregateId={}, version={}",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.key(),
                    message.getEventId(),
                    message.getEventType(),
                    message.getAggregateId(),
                    message.getAggregateVersion()
            );

            process(message);

            log.info(
                    "[KAFKA_CONSUMER] Consumed event successfully. messageId={}, eventType={}, aggregateId={}, version={}",
                    message.getEventId(),
                    message.getEventType(),
                    message.getAggregateId(),
                    message.getAggregateVersion()
            );
        } catch (Exception exception) {
            log.error(
                    "[KAFKA_CONSUMER] Failed to consume event. messageId={}, eventType={}, aggregateId={}, version={}",
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
        if (messageDedupService.isProcessed(message.getEventId())) {
            log.info(
                    "[KAFKA_CONSUMER] Message already processed. Skip. messageId={}, eventType={}, aggregateId={}",
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

    private String resolveCorrelationId(DomainEventMessage message) {
        if (message.getCorrelationId() == null || message.getCorrelationId().isBlank()) {
            return "unknown";
        }

        return message.getCorrelationId();
    }
}
