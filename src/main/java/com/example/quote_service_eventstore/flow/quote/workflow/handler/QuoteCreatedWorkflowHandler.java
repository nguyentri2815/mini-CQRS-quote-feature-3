package com.example.quote_service_eventstore.flow.quote.workflow.handler;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.domain.quote.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.flow.quote.workflow.QuoteSyncWorkflow;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(200)
public class QuoteCreatedWorkflowHandler implements DomainEventHandler<QuoteCreatedEvent> {

    private final QuoteSyncWorkflow quoteSyncWorkflow;

    public QuoteCreatedWorkflowHandler(QuoteSyncWorkflow quoteSyncWorkflow) {
        this.quoteSyncWorkflow = quoteSyncWorkflow;
    }

    @Override
    public Class<QuoteCreatedEvent> eventType() {
        return QuoteCreatedEvent.class;
    }

    @Override
    public void handle(DomainEventEnvelope<QuoteCreatedEvent> envelope) {
        quoteSyncWorkflow.onQuoteCreated(envelope.getEvent());
    }
}
