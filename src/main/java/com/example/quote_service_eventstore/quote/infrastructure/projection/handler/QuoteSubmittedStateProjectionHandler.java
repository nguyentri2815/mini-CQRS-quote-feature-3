package com.example.quote_service_eventstore.quote.infrastructure.projection.handler;

import com.example.quote_service_eventstore.common.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import org.springframework.stereotype.Component;

@Component
public class QuoteSubmittedStateProjectionHandler implements DomainEventHandler<QuoteSubmittedEvent> {

    private final QuoteStateProjectionHandler quoteStateProjectionHandler;

    public QuoteSubmittedStateProjectionHandler(
            QuoteStateProjectionHandler quoteStateProjectionHandler
    ) {
        this.quoteStateProjectionHandler = quoteStateProjectionHandler;
    }

    @Override
    public Class<QuoteSubmittedEvent> eventType() {
        return QuoteSubmittedEvent.class;
    }

    @Override
    public void handle(QuoteSubmittedEvent event) {
        quoteStateProjectionHandler.project(event);
    }
}
