package com.example.quote_service_eventstore.flow.quote.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuoteNotificationService {

    private static final Logger log = LoggerFactory.getLogger(QuoteNotificationService.class);

    public void notifyQuoteCreated(String quoteId) {
        log.info("[NOTIFICATION] Quote created. quoteId={}", quoteId);
    }

    public void notifyQuoteSubmitted(String quoteId) {
        log.info("[NOTIFICATION] Quote submitted. quoteId={}", quoteId);
    }

    public void notifyQuoteApproved(String quoteId) {
        log.info("[NOTIFICATION] Quote approved. quoteId={}", quoteId);
    }
}
