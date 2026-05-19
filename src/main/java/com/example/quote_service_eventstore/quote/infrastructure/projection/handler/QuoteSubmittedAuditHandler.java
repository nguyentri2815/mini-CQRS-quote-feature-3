package com.example.quote_service_eventstore.quote.infrastructure.projection.handler;

import com.example.quote_service_eventstore.common.eventbus.DomainEventHandler;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
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
    public void handle(QuoteSubmittedEvent event) {
        log.info(
                "[AUDIT] Quote submitted. quoteId={}, submittedBy={}, occurredAt={}",
                event.getQuoteId(),
                event.getSubmittedBy(),
                event.occurredAt()
        );
    }
}
