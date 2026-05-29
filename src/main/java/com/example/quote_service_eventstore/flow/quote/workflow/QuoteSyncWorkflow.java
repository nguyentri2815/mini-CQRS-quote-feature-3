package com.example.quote_service_eventstore.flow.quote.workflow;

import com.example.quote_service_eventstore.domain.quote.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.domain.quote.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.domain.quote.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.flow.quote.infrastructure.allocation.QuoteAllocationGateway;
import com.example.quote_service_eventstore.flow.quote.infrastructure.notification.QuoteNotificationService;
import com.example.quote_service_eventstore.flow.quote.infrastructure.search.service.QuoteIndexService;
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

        log.info("[WORKFLOW] Started. eventType=QuoteCreatedEvent, quoteId={}", quoteId);

        log.info("[WORKFLOW] Step started. eventType=QuoteCreatedEvent, quoteId={}, step=ES_SYNC", quoteId);
        quoteIndexService.syncQuote(quoteId);
        log.info("[WORKFLOW] Step completed. eventType=QuoteCreatedEvent, quoteId={}, step=ES_SYNC", quoteId);

        log.info("[WORKFLOW] Step started. eventType=QuoteCreatedEvent, quoteId={}, step=NOTIFICATION", quoteId);
        quoteNotificationService.notifyQuoteCreated(quoteId);
        log.info("[WORKFLOW] Step completed. eventType=QuoteCreatedEvent, quoteId={}, step=NOTIFICATION", quoteId);

        log.info("[WORKFLOW] Completed. eventType=QuoteCreatedEvent, quoteId={}", quoteId);
    }

    public void onQuoteSubmitted(QuoteSubmittedEvent event) {
        String quoteId = event.getQuoteId();

        log.info("[WORKFLOW] Started. eventType=QuoteSubmittedEvent, quoteId={}", quoteId);

        log.info("[WORKFLOW] Step started. eventType=QuoteSubmittedEvent, quoteId={}, step=ES_SYNC", quoteId);
        quoteIndexService.syncQuote(quoteId);
        log.info("[WORKFLOW] Step completed. eventType=QuoteSubmittedEvent, quoteId={}, step=ES_SYNC", quoteId);

        log.info("[WORKFLOW] Step started. eventType=QuoteSubmittedEvent, quoteId={}, step=NOTIFICATION", quoteId);
        quoteNotificationService.notifyQuoteSubmitted(quoteId);
        log.info("[WORKFLOW] Step completed. eventType=QuoteSubmittedEvent, quoteId={}, step=NOTIFICATION", quoteId);

        log.info("[WORKFLOW] Step started. eventType=QuoteSubmittedEvent, quoteId={}, step=ALLOCATION", quoteId);
        quoteAllocationGateway.sendSubmitAllocation(quoteId);
        log.info("[WORKFLOW] Step completed. eventType=QuoteSubmittedEvent, quoteId={}, step=ALLOCATION", quoteId);

        log.info("[WORKFLOW] Completed. eventType=QuoteSubmittedEvent, quoteId={}", quoteId);
    }

    public void onQuoteApproved(QuoteApprovedEvent event) {
        String quoteId = event.getQuoteId();

        log.info("[WORKFLOW] Started. eventType=QuoteApprovedEvent, quoteId={}", quoteId);

        log.info("[WORKFLOW] Step started. eventType=QuoteApprovedEvent, quoteId={}, step=ES_SYNC", quoteId);
        quoteIndexService.syncQuote(quoteId);
        log.info("[WORKFLOW] Step completed. eventType=QuoteApprovedEvent, quoteId={}, step=ES_SYNC", quoteId);

        log.info("[WORKFLOW] Step started. eventType=QuoteApprovedEvent, quoteId={}, step=NOTIFICATION", quoteId);
        quoteNotificationService.notifyQuoteApproved(quoteId);
        log.info("[WORKFLOW] Step completed. eventType=QuoteApprovedEvent, quoteId={}, step=NOTIFICATION", quoteId);

        log.info("[WORKFLOW] Step started. eventType=QuoteApprovedEvent, quoteId={}, step=ALLOCATION", quoteId);
        quoteAllocationGateway.sendApproveAllocation(quoteId);
        log.info("[WORKFLOW] Step completed. eventType=QuoteApprovedEvent, quoteId={}, step=ALLOCATION", quoteId);

        log.info("[WORKFLOW] Completed. eventType=QuoteApprovedEvent, quoteId={}", quoteId);
    }
}
