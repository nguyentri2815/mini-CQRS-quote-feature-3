package com.example.quote_service_eventstore.flow.quote.consumer.kafka;

import com.example.quote_service_eventstore.flow.quote.consumer.DomainEventMessageProcessor;
import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.kafka.QuoteKafkaTopicNames;
import com.example.quote_service_eventstore.shared.observability.ObservabilityConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!rabbit")
public class QuoteKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(QuoteKafkaConsumer.class);

    private final DomainEventMessageProcessor processor;

    public QuoteKafkaConsumer(DomainEventMessageProcessor processor) {
        this.processor = processor;
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

            processor.process(message);

            log.info(
                    "[KAFKA_CONSUMER] Consumed event successfully. messageId={}, eventType={}, aggregateId={}, version={}",
                    message.getEventId(),
                    message.getEventType(),
                    message.getAggregateId(),
                    message.getAggregateVersion()
            );
        } catch (Exception exception) {
            log.error(
                    "[KAFKA_CONSUMER] Failed to consume event. topic={}, partition={}, offset={}, key={}, messageId={}, eventType={}, aggregateId={}, version={}",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.key(),
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

    private String resolveCorrelationId(DomainEventMessage message) {
        if (message.getCorrelationId() == null || message.getCorrelationId().isBlank()) {
            return "unknown";
        }

        return message.getCorrelationId();
    }
}
