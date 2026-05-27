package com.example.quote_service_eventstore.shared.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainEventRabbitConfig {

    public static final String DOMAIN_EVENT_EXCHANGE = "domain.event.exchange";
    public static final String DOMAIN_EVENT_DLX = "domain.event.dlx";

    public static final String QUOTE_EVENT_QUEUE = "quote.event.queue";
    public static final String QUOTE_EVENT_DLQ = "quote.event.dlq";

    public static final String QUOTE_EVENT_ROUTING_KEY = "quote.event";
    public static final String QUOTE_EVENT_DLQ_ROUTING_KEY = "quote.event.dlq";

    @Bean
    public TopicExchange domainEventExchange() {
        return new TopicExchange(DOMAIN_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange domainEventDeadLetterExchange() {
        return new TopicExchange(DOMAIN_EVENT_DLX, true, false);
    }

    @Bean
    public Queue quoteEventQueue() {
        return QueueBuilder
                .durable(QUOTE_EVENT_QUEUE)
                .deadLetterExchange(DOMAIN_EVENT_DLX)
                .deadLetterRoutingKey(QUOTE_EVENT_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue quoteEventDeadLetterQueue() {
        return QueueBuilder
                .durable(QUOTE_EVENT_DLQ)
                .build();
    }

    @Bean
    public Binding quoteEventBinding() {
        return BindingBuilder
                .bind(quoteEventQueue())
                .to(domainEventExchange())
                .with(QUOTE_EVENT_ROUTING_KEY);
    }

    @Bean
    public Binding quoteEventDeadLetterBinding() {
        return BindingBuilder
                .bind(quoteEventDeadLetterQueue())
                .to(domainEventDeadLetterExchange())
                .with(QUOTE_EVENT_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
