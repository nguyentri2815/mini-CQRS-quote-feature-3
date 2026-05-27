package com.example.quote_service_eventstore.domain.quote.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuoteCreatedEvent implements DomainEvent {

    private final String quoteId;
    private final String customerName;
    private final String productCode;
    private final BigDecimal premium;

    private final String createdBy;
    private final String createdByName;
    private final String tenantId;
    private final String organizationId;

    private final LocalDateTime occurredAt;

    public QuoteCreatedEvent(
            String quoteId,
            String customerName,
            String productCode,
            BigDecimal premium,
            String createdBy,
            String createdByName,
            String tenantId,
            String organizationId,
            LocalDateTime occurredAt
    ) {
        this.quoteId = quoteId;
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
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
        return "QuoteCreatedEvent";
    }

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductCode() {
        return productCode;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}

