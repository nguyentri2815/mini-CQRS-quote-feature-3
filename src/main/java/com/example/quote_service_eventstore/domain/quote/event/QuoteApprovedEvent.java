package com.example.quote_service_eventstore.domain.quote.event;

import java.time.LocalDateTime;

public class QuoteApprovedEvent implements DomainEvent {

    private final String quoteId;
    private final String approvedBy;
    private final String approvedByName;
    private final String tenantId;
    private final String organizationId;
    private final LocalDateTime occurredAt;

    public QuoteApprovedEvent(
            String quoteId,
            String approvedBy,
            String approvedByName,
            String tenantId,
            String organizationId,
            LocalDateTime occurredAt
    ) {
        this.quoteId = quoteId;
        this.approvedBy = approvedBy;
        this.approvedByName = approvedByName;
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
        return "QuoteApprovedEvent";
    }

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public String getApprovedByName() {
        return approvedByName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
