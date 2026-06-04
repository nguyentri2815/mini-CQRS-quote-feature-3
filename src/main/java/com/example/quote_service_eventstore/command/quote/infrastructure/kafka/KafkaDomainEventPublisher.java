package com.example.quote_service_eventstore.command.quote.infrastructure.kafka;

import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.kafka.QuoteKafkaTopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);

    private final KafkaTemplate<String, DomainEventMessage> kafkaTemplate;

    public KafkaDomainEventPublisher(
            KafkaTemplate<String, DomainEventMessage> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(DomainEventMessage message) {
        String key = message.getAggregateId();

        try {
            kafkaTemplate.send(
                    QuoteKafkaTopicNames.QUOTE_EVENTS,
                    key,
                    message
            ).get();

            log.info(
                    "[KAFKA_PRODUCER] Published domain event. topic={}, key={}, eventId={}, eventType={}, aggregateId={}, version={}",
                    QuoteKafkaTopicNames.QUOTE_EVENTS,
                    key,
                    message.getEventId(),
                    message.getEventType(),
                    message.getAggregateId(),
                    message.getAggregateVersion()
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to publish domain event to Kafka. eventId="
                            + message.getEventId()
                            + ", aggregateId="
                            + message.getAggregateId(),
                    exception
            );
        }
    }

}
