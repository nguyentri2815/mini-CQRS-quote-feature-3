package com.example.quote_service_eventstore.flow.quote.workflow.handler;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.domain.quote.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.flow.quote.workflow.QuoteSyncWorkflow;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(200)
public class QuoteSubmittedWorkflowHandler implements DomainEventHandler<QuoteSubmittedEvent> {

    private final QuoteSyncWorkflow quoteSyncWorkflow;

    public QuoteSubmittedWorkflowHandler(QuoteSyncWorkflow quoteSyncWorkflow) {
        this.quoteSyncWorkflow = quoteSyncWorkflow;
    }

    @Override
    public Class<QuoteSubmittedEvent> eventType() {
        return QuoteSubmittedEvent.class;
    }

    @Override
    public void handle(DomainEventEnvelope<QuoteSubmittedEvent> envelope) {
        quoteSyncWorkflow.onQuoteSubmitted(envelope.getEvent());
    }
}

