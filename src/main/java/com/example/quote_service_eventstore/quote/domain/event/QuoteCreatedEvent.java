package com.example.quote_service_eventstore.quote.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuoteCreatedEvent implements DomainEvent {

    private final String quoteId;
    private final String customerName;
    private final String productCode;
    private final BigDecimal premium;
    private final String createdBy;
    private final LocalDateTime occurredAt;

    public QuoteCreatedEvent(
            String quoteId,
            String customerName,
            String productCode,
            BigDecimal premium,
            String createdBy,
            LocalDateTime occurredAt
    ) {
        this.quoteId = quoteId;
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.createdBy = createdBy;
        this.occurredAt = occurredAt;
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

    @Override
    public String eventName() {
        return "QuoteCreatedEvent";
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
