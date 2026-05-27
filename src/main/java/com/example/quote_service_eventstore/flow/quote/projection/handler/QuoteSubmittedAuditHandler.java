package com.example.quote_service_eventstore.flow.quote.projection.handler;

import com.example.quote_service_eventstore.shared.eventbus.DomainEventEnvelope;
import com.example.quote_service_eventstore.shared.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.domain.quote.event.QuoteSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuoteSubmittedAuditHandler implements DomainEventHandler<QuoteSubmittedEvent> {

    private static final Logger log = LoggerFactory.getLogger(QuoteSubmittedAuditHandler.class);

    @Override
    public Class<QuoteSubmittedEvent> eventType() {
        return QuoteSubmittedEvent.class;
    }

    @Override
    public void handle(DomainEventEnvelope<QuoteSubmittedEvent> envelope) {
        QuoteSubmittedEvent event = envelope.getEvent();
        long version = envelope.getAggregateVersion();

        log.info(
                "[AUDIT] Quote submitted. quoteId={}, submittedBy={}, occurredAt={}, version{}",
                event.getQuoteId(),
                event.getSubmittedBy(),
                event.occurredAt(),
                version
        );
    }
}
