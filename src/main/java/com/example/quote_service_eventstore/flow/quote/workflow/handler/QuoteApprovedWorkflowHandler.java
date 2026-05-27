package com.example.quote_service_eventstore.flow.quote.workflow.handler;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.domain.quote.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.flow.quote.workflow.QuoteSyncWorkflow;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(200)
public class QuoteApprovedWorkflowHandler implements DomainEventHandler<QuoteApprovedEvent> {

    private final QuoteSyncWorkflow quoteSyncWorkflow;

    public QuoteApprovedWorkflowHandler(QuoteSyncWorkflow quoteSyncWorkflow) {
        this.quoteSyncWorkflow = quoteSyncWorkflow;
    }

    @Override
    public Class<QuoteApprovedEvent> eventType() {
        return QuoteApprovedEvent.class;
    }

    @Override
    public void handle( DomainEventEnvelope<QuoteApprovedEvent> envelope) {
        quoteSyncWorkflow.onQuoteApproved(envelope.getEvent());
    }
}
