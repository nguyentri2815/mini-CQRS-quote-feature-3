package com.example.quote_service_eventstore.quote.infrastructure.projection.handler;

import com.example.quote_service_eventstore.common.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.common.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.quote.domain.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.quote.infrastructure.projection.handler.QuoteStateProjectionHandler;
import org.springframework.stereotype.Component;

@Component
public class QuoteApprovedStateProjectionHandler implements DomainEventHandler<QuoteApprovedEvent> {

    private final QuoteStateProjectionHandler quoteStateProjectionHandler;

    public QuoteApprovedStateProjectionHandler(
            QuoteStateProjectionHandler quoteStateProjectionHandler
    ) {
        this.quoteStateProjectionHandler = quoteStateProjectionHandler;
    }

    @Override
    public Class<QuoteApprovedEvent> eventType() {
        return QuoteApprovedEvent.class;
    }

    @Override
    public void handle(DomainEventEnvelope<QuoteApprovedEvent> envelope) {
        quoteStateProjectionHandler.project(
                envelope.getEvent(),
                envelope.getAggregateVersion()
        );
    }
}

