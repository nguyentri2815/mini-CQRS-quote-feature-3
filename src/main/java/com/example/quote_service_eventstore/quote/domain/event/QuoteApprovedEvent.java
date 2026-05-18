package com.example.quote_service_eventstore.quote.domain.event;

import java.time.LocalDateTime;

public class QuoteApprovedEvent implements DomainEvent {

    private final String quoteId;
    private final String approvedBy;
    private final LocalDateTime occurredAt;

    public QuoteApprovedEvent(
            String quoteId,
            String approvedBy,
            LocalDateTime occurredAt
    ) {
        this.quoteId = quoteId;
        this.approvedBy = approvedBy;
        this.occurredAt = occurredAt;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    @Override
    public String eventName() {
        return "QuoteApprovedEvent";
    }

    @Override
    public String aggregateId() {
        return quoteId;
    }

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}
