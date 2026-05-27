package com.example.quote_service_eventstore.flow.quote.projection.handler;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.domain.quote.event.QuoteCreatedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
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
    public void handle(DomainEventEnvelope<QuoteCreatedEvent> envelope) {
        quoteStateProjectionHandler.project(
                envelope.getEvent(),
                envelope.getAggregateVersion()
        );
    }
}
