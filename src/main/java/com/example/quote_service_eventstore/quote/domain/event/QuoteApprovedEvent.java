package com.example.quote_service_eventstore.quote.domain.event;

import java.time.LocalDateTime;

public class QuoteApprovedEvent implements DomainEvent {

    private String quoteId;
    private String approvedBy;
    private LocalDateTime occurredAt;

    public QuoteApprovedEvent() {
    }

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

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
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
