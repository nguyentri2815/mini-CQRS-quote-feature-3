package com.example.quote_service_eventstore.common.messaging;

import com.example.quote_service_eventstore.common.eventbus.DomainEventPublisher;
import com.example.quote_service_eventstore.common.eventstore.EventSerializer;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//@Primary: ngày 14 không dùng trực tiếp trong CommandService nữa.
public class RabbitMqDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqDomainEventPublisher.class);

    private static final String QUOTE_AGGREGATE_TYPE = "Quote";

    private final RabbitTemplate rabbitTemplate;
    private final EventSerializer eventSerializer;

    public RabbitMqDomainEventPublisher(
            RabbitTemplate rabbitTemplate,
            EventSerializer eventSerializer
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventSerializer = eventSerializer;
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
                event.occurredAt()
        );

        log.info(
                "[RABBIT_PUBLISHER] Publishing event message. eventType={}, aggregateId={}",
                message.getEventType(),
                message.getAggregateId()
        );

        rabbitTemplate.convertAndSend(
                DomainEventRabbitConfig.DOMAIN_EVENT_EXCHANGE,
                DomainEventRabbitConfig.QUOTE_EVENT_ROUTING_KEY,
                message
        );
    }
}
