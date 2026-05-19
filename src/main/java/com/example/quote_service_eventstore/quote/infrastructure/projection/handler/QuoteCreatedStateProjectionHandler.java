package com.example.quote_service_eventstore.quote.infrastructure.projection.handler;

import com.example.quote_service_eventstore.common.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class QuoteCreatedStateProjectionHandler implements DomainEventHandler<QuoteCreatedEvent> {

    private final QuoteStateProjectionHandler quoteStateProjectionHandler;

    public QuoteCreatedStateProjectionHandler(
            QuoteStateProjectionHandler quoteStateProjectionHandler
    ) {
        this.quoteStateProjectionHandler = quoteStateProjectionHandler;
    }

    @Override
    public Class<QuoteCreatedEvent> eventType() {
        return QuoteCreatedEvent.class;
    }

    @Override
    public void handle(QuoteCreatedEvent event) {
        quoteStateProjectionHandler.project(event);
    }
}
