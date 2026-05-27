package com.example.quote_service_eventstore.flow.quote.projection.handler;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.domain.quote.event.QuoteApprovedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
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

