package com.example.quote_service_eventstore.quote.workflow;

import com.example.quote_service_eventstore.quote.domain.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.quote.infrastructure.allocation.QuoteAllocationGateway;
import com.example.quote_service_eventstore.quote.infrastructure.notification.QuoteNotificationService;
import com.example.quote_service_eventstore.quote.infrastructure.search.service.QuoteIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuoteSyncWorkflow {

    private static final Logger log = LoggerFactory.getLogger(QuoteSyncWorkflow.class);

    private final QuoteIndexService quoteIndexService;
    private final QuoteNotificationService quoteNotificationService;
    private final QuoteAllocationGateway quoteAllocationGateway;

    public QuoteSyncWorkflow(
            QuoteIndexService quoteIndexService,
            QuoteNotificationService quoteNotificationService,
            QuoteAllocationGateway quoteAllocationGateway
    ) {
        this.quoteIndexService = quoteIndexService;
        this.quoteNotificationService = quoteNotificationService;
        this.quoteAllocationGateway = quoteAllocationGateway;
    }

    public void onQuoteCreated(QuoteCreatedEvent event) {
        String quoteId = event.getQuoteId();

        log.info("[WORKFLOW] Handling QuoteCreatedEvent. quoteId={}", quoteId);

        quoteIndexService.syncQuote(quoteId);
        quoteNotificationService.notifyQuoteCreated(quoteId);
    }

    public void onQuoteSubmitted(QuoteSubmittedEvent event) {
        String quoteId = event.getQuoteId();

        log.info("[WORKFLOW] Handling QuoteSubmittedEvent. quoteId={}", quoteId);

        quoteIndexService.syncQuote(quoteId);
        quoteNotificationService.notifyQuoteSubmitted(quoteId);
        quoteAllocationGateway.sendSubmitAllocation(quoteId);
    }

    public void onQuoteApproved(QuoteApprovedEvent event) {
        String quoteId = event.getQuoteId();

        log.info("[WORKFLOW] Handling QuoteApprovedEvent. quoteId={}", quoteId);

        quoteIndexService.syncQuote(quoteId);
        quoteNotificationService.notifyQuoteApproved(quoteId);
        quoteAllocationGateway.sendApproveAllocation(quoteId);
    }
}
