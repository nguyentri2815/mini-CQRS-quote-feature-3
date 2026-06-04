package com.example.quote_service_eventstore.shared.messaging.kafka;

import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler kafkaDefaultErrorHandler(
            KafkaTemplate<String, DomainEventMessage> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        this::resolveDeadLetterTopic
                );

        FixedBackOff fixedBackOff = new FixedBackOff(
                1000L,
                3L
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                fixedBackOff
        );

        return errorHandler;
    }

    private TopicPartition resolveDeadLetterTopic(
            ConsumerRecord<?, ?> record,
            Exception exception
    ) {
        return new TopicPartition(
                QuoteKafkaTopicNames.QUOTE_EVENTS_DLT,
                record.partition()
        );
    }
}
