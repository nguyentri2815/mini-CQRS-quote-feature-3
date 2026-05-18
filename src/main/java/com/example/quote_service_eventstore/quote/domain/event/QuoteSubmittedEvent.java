package com.example.quote_service_eventstore.quote.domain.event;

import java.time.LocalDateTime;

public class QuoteSubmittedEvent implements DomainEvent {

    private String quoteId;
    private String submittedBy;
    private LocalDateTime occurredAt;

    public QuoteSubmittedEvent() {
    }

    public QuoteSubmittedEvent(
            String quoteId,
            String submittedBy,
            LocalDateTime occurredAt
    ) {
        this.quoteId = quoteId;
        this.submittedBy = submittedBy;
        this.occurredAt = occurredAt;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    @Override
    public String eventName() {
        return "QuoteSubmittedEvent";
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

