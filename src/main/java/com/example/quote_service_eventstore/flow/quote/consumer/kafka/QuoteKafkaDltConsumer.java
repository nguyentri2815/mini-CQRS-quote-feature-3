package com.example.quote_service_eventstore.flow.quote.consumer.kafka;

import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.kafka.QuoteKafkaTopicNames;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class QuoteKafkaDltConsumer {

    private static final Logger log = LoggerFactory.getLogger(QuoteKafkaDltConsumer.class);

    @KafkaListener(
            topics = QuoteKafkaTopicNames.QUOTE_EVENTS_DLT,
            groupId = "quote-flow-service-dlt-debug"
    )
    public void consumeDlt(
            DomainEventMessage message,
            ConsumerRecord<String, DomainEventMessage> record
    ) {
        log.error(
                "[KAFKA_DLT] Received dead-letter event. topic={}, partition={}, offset={}, key={}, messageId={}, eventType={}, aggregateId={}, version={}, correlationId={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                message.getEventId(),
                message.getEventType(),
                message.getAggregateId(),
                message.getAggregateVersion(),
                message.getCorrelationId()
        );
    }
}
