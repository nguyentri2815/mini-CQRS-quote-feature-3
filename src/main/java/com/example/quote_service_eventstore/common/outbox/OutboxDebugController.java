package com.example.quote_service_eventstore.common.outbox;

import com.example.quote_service_eventstore.common.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.common.messaging.DomainEventRabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug/outbox")
public class OutboxDebugController {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxDebugController(
            OutboxEventRepository outboxEventRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/{outboxId}/republish")
    public String republish(@PathVariable String outboxId) {
        OutboxEventEntity outboxEvent = outboxEventRepository.findById(outboxId)
                .orElseThrow(() -> new IllegalArgumentException("Outbox event not found: " + outboxId));

        DomainEventMessage message = new DomainEventMessage(
                outboxEvent.getId(),
                outboxEvent.getAggregateId(),
                outboxEvent.getAggregateType(),
                outboxEvent.getEventType(),
                outboxEvent.getPayload(),
                outboxEvent.getAggregateVersion(),
                outboxEvent.getCreatedAt()
        );

        rabbitTemplate.convertAndSend(
                DomainEventRabbitConfig.DOMAIN_EVENT_EXCHANGE,
                DomainEventRabbitConfig.QUOTE_EVENT_ROUTING_KEY,
                message
        );

        return "republished outbox event: " + outboxId;
    }
}
