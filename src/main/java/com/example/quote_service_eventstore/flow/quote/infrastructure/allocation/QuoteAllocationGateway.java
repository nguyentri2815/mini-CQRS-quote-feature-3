package com.example.quote_service_eventstore.flow.quote.infrastructure.allocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuoteAllocationGateway {

    private static final Logger log = LoggerFactory.getLogger(QuoteAllocationGateway.class);

    public void sendSubmitAllocation(String quoteId) {
        log.info("[ALLOCATION] Send submit allocation request. quoteId={}", quoteId);
    }

    public void sendApproveAllocation(String quoteId) {
        log.info("[ALLOCATION] Send approve allocation request. quoteId={}", quoteId);
    }
}
