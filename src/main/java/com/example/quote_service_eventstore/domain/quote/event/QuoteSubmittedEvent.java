package com.example.quote_service_eventstore.domain.quote.event;

import java.time.LocalDateTime;

public class QuoteSubmittedEvent implements DomainEvent {

    private final String quoteId;
    private final String submittedBy;
    private final String submittedByName;
    private final String tenantId;
    private final String organizationId;
    private final LocalDateTime occurredAt;

    public QuoteSubmittedEvent(
            String quoteId,
            String submittedBy,
            String submittedByName,
            String tenantId,
            String organizationId,
            LocalDateTime occurredAt
    ) {
        this.quoteId = quoteId;
        this.submittedBy = submittedBy;
        this.submittedByName = submittedByName;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String aggregateId() {
        return quoteId;
    }

    @Override
    public String eventName() {
        return "QuoteSubmittedEvent";
    }

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public String getSubmittedByName() {
        return submittedByName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}

