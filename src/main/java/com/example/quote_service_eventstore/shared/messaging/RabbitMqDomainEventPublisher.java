package com.example.quote_service_eventstore.shared.messaging;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventPublisher;
import com.example.quote_service_eventstore.shared.eventstore.EventSerializer;
import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import com.example.quote_service_eventstore.shared.observability.CorrelationIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//@Primary: ngày 14 không dùng trực tiếp trong CommandService nữa.
public class RabbitMqDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqDomainEventPublisher.class);

    private static final String QUOTE_AGGREGATE_TYPE = "Quote";

    private final RabbitTemplate rabbitTemplate;
    private final EventSerializer eventSerializer;
    private final CorrelationIdProvider correlationIdProvider;

    public RabbitMqDomainEventPublisher(
            RabbitTemplate rabbitTemplate,
            EventSerializer eventSerializer,
            CorrelationIdProvider correlationIdProvider
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventSerializer = eventSerializer;
        this.correlationIdProvider = correlationIdProvider;
    }

    @Override
    public void publish(DomainEvent event) {
        DomainEventMessage message = new DomainEventMessage(
                UUID.randomUUID().toString(),
                event.aggregateId(),
                QUOTE_AGGREGATE_TYPE,
                event.eventName(),
                eventSerializer.serialize(event),
                1,//test
                correlationIdProvider.getCorrelationId(),
                event.occurredAt()
        );

        log.info(
                "[RABBIT_PUBLISHER] Publishing event message. eventType={}, aggregateId={}, correlationId={}",
                message.getEventType(),
                message.getAggregateId(),
                message.getCorrelationId()
        );

        rabbitTemplate.convertAndSend(
                DomainEventRabbitConfig.DOMAIN_EVENT_EXCHANGE,
                DomainEventRabbitConfig.QUOTE_EVENT_ROUTING_KEY,
                message
        );
    }
}
