package com.example.quote_service_eventstore.quote.workflow.handler;

import com.example.quote_service_eventstore.common.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.common.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.quote.domain.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.quote.workflow.QuoteSyncWorkflow;
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
