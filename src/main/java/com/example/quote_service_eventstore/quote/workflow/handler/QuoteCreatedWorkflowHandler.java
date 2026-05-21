package com.example.quote_service_eventstore.quote.workflow.handler;

import com.example.quote_service_eventstore.common.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.common.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.quote.workflow.QuoteSyncWorkflow;
import org.springframework.stereotype.Component;

@Component
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
