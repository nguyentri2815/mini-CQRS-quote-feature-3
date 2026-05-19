package com.example.quote_service_eventstore.common.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainEventRabbitConfig {

    public static final String DOMAIN_EVENT_EXCHANGE = "domain.event.exchange";

    public static final String QUOTE_EVENT_QUEUE = "quote.event.queue";

    public static final String QUOTE_EVENT_ROUTING_KEY = "quote.event";

    @Bean
    public TopicExchange domainEventExchange() {
        return new TopicExchange(DOMAIN_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue quoteEventQueue() {
        return new Queue(QUOTE_EVENT_QUEUE, true);
    }

    @Bean
    public Binding quoteEventBinding() {
        return BindingBuilder
                .bind(quoteEventQueue())
                .to(domainEventExchange())
                .with(QUOTE_EVENT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
